/* 
 * Copyright (C) 2013 Jonathan Smith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jonathansmith.javadpad.engine.local;

import net.jonathansmith.javadpad.database.DatabaseConnection;
import net.jonathansmith.javadpad.engine.DPADClientEngine;
import net.jonathansmith.javadpad.engine.common.process.RuntimeProcess;
import net.jonathansmith.javadpad.engine.local.process.AnalyseDisplayProcess;
import net.jonathansmith.javadpad.engine.local.process.LoadProcessProcess;
import net.jonathansmith.javadpad.engine.local.process.Startup_LocalProcess;
import net.jonathansmith.javadpad.plugin.DPADPluginManager;
import net.jonathansmith.javadpad.util.FileSystem;
import net.jonathansmith.javadpad.util.RuntimeType;
import net.jonathansmith.javadpad.util.ThreadType;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 * DPADEngine
 *
 * @author Jonathan Smith
 */
public class DPADLocalEngine extends DPADClientEngine {
    
    public boolean status = false;
    public boolean errored = false;
    public boolean running = false;
    
    public boolean hasUser = false;
    public boolean hasExperiment = false;
    
    private RuntimeType currentRuntime;
    private RuntimeProcess runtime;
    
    private DatabaseConnection session = null;
    private DPADPluginManager pluginManager = null;
    
    public DPADLocalEngine(FileSystem fileSystem) {
        super(fileSystem);
    }
    
    public void init() {
        this.status = true;
        this.setRuntime(RuntimeType.SETUP_CLIENT);
    }
    
    @SuppressWarnings({"CallToThreadDumpStack", "SleepWhileInLoop"})
    @Override
    public void run() {
        while (this.status) {
            if (this.errored) {
                DPADLogger.severe("Exception in worker, runtime execution halted");
                this.status = false;
                continue;
            }
            
            while (!this.currentRuntime.isRunnable()) {
                try {
                    Thread.sleep(100);
                } catch (Throwable t) {
                    DPADLogger.severe("Exception in worker, runtime execution halted");
                    DPADLogger.logStackTrace(t);
                }
            }
            
            if (!this.running) {
                this.runtime.start();
                this.running = true;
                
            } else {
                while (this.running) {
                    try {
                        Thread.sleep(100);
                        
                    } catch (Throwable t) {
                        DPADLogger.severe("Exception in worker, runtime execution halted");
                        DPADLogger.logStackTrace(t);
                    }
                }
            }
        }
    }
    
    public void setupEngine() {
        if (this.currentRuntime != RuntimeType.SETUP_CLIENT 
            || !(this.runtime instanceof Startup_LocalProcess)
            || ((Startup_LocalProcess) this.runtime).isConnected()) {
            return;
        }
        
        Startup_LocalProcess setup = (Startup_LocalProcess) this.runtime;
        this.session = setup.getDatabaseConnection();
        this.pluginManager = setup.getPluginManager();
        this.sendQuitToRuntime();
    }
    
    public synchronized RuntimeType getCurrentRuntime() {
        DPADLogger.info("Retrieving current runtime");
        return this.currentRuntime;
    }
    
    public synchronized RuntimeProcess getRuntime() {
        DPADLogger.info("Retrieving runtime");
        return this.runtime;
    }
    
    public void setRuntime(RuntimeType runtime) {
        if (!this.status) {
            DPADLogger.severe("Cannot switch runtimes when the engine has crashed");
            return;
        }
        
        if (this.errored) {
            DPADLogger.severe("Cannot continue as the engine has thrown an unhandled error");
            return;
        }
        
        if (this.runtime != null && this.runtime.isAlive()) {
            DPADLogger.severe("Cannot change runtime as it is currently in: " + this.currentRuntime.toString());
            return;
        }
        
        switch (runtime) {
            case SETUP_CLIENT:           this.runtime = new Startup_LocalProcess(this);
                                        break;
            
            case LOAD_AND_PROCESS:      this.runtime = new LoadProcessProcess(this);
                                        break;
                
            case ANALYSE_AND_DISPLAY:   this.runtime = new AnalyseDisplayProcess(this);
                                        break;
                
            default:                    break;
        }
        
        this.currentRuntime = runtime;
        if (this.currentRuntime.isRunnable()) {
            this.runtime.init();
        }
        
        this.setChanged();
        this.notifyObservers();
        DPADLogger.info("Notified observers of engine changed to: " + this.currentRuntime.toString());
    }

    @Override
    public ThreadType getThreadType() {
        return ThreadType.LOCAL;
    }
    
    @Override
    public void sendQuitToRuntime() {
        DPADLogger.info("Forcing runtime shutdown of current thread, assumed reason: back was called");
        if (this.currentRuntime != RuntimeType.IDLE_CLIENT && this.currentRuntime.isRunnable()) {
            this.runtime.forceShutdown(false);
        }
        
        else if (this.currentRuntime != RuntimeType.IDLE_CLIENT && !this.currentRuntime.isRunnable()) {
            this.runtimeFinished(false);
        }
    }
    
    public void runtimeFinished(boolean status) {
        this.runtime = null;
        this.currentRuntime = RuntimeType.IDLE_CLIENT;
        this.errored = status;
        this.running = false;
        
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void quitEngine() {
        this.sendQuitToRuntime();
        this.status = false;
    }
}
