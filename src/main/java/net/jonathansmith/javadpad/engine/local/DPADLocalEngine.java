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

import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.database.DatabaseConnection;
import net.jonathansmith.javadpad.engine.database.entry.ExperimentEntry;
import net.jonathansmith.javadpad.engine.database.entry.UserEntry;
import net.jonathansmith.javadpad.engine.local.process.AnalyseDisplayProcess;
import net.jonathansmith.javadpad.engine.local.process.LocalStartupProcess;
import net.jonathansmith.javadpad.engine.local.process.LoadProcessProcess;
import net.jonathansmith.javadpad.engine.local.process.RuntimeProcess;
import net.jonathansmith.javadpad.util.RuntimeType;
import net.jonathansmith.javadpad.engine.local.process.UserSetProcess;
import net.jonathansmith.javadpad.util.logging.DPADLogger;
import net.jonathansmith.javadpad.util.ThreadType;

/**
 * DPADEngine
 *
 * @author Jonathan Smith
 */
public class DPADLocalEngine extends DPADEngine {
    
    public static final int PROGRESS_MAX = 20;
    
    public boolean status;
    public boolean errored = false;
    public boolean running = false;
    public boolean hasUser = false;
    public boolean hasExperiment = false;
    public int progress = 0;
    
    private RuntimeType currentRuntime;
    private RuntimeProcess runtime;
    private UserEntry user = null;
    private ExperimentEntry experiment = null;
    private DatabaseConnection session = null;
    
    public DPADLocalEngine(DPADLogger logger) {
        super(logger);
    }
    
    public void init() {
        this.status = true;
        this.setRuntime(RuntimeType.FILE_CONNECT);
    }
    
    @SuppressWarnings({"CallToThreadDumpStack", "SleepWhileInLoop"})
    @Override
    public void run() {
        while (this.status) {
            if (this.errored) {
                this.logger.severe("Exception in worker, runtime execution halted");
                this.status = false;
                continue;
            }
            
            while (!this.currentRuntime.isRunnable()) {
                try {
                    Thread.sleep(100);
                } catch (Throwable t) {
                    this.logger.severe("Exception in worker, runtime execution halted");
                    t.printStackTrace();
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
                        this.logger.severe("Exception in worker, runtime execution halted");
                        t.printStackTrace();
                    }
                }
            }
        }
    }
    
    public synchronized RuntimeType getCurrentRuntime() {
        this.logger.info("Retrieving current runtime");
        return this.currentRuntime;
    }
    
    public synchronized RuntimeProcess getRuntime() {
        this.logger.info("Retrieving runtime");
        return this.runtime;
    }
    
    public void setRuntime(RuntimeType runtime) {
        if (!this.status) {
            this.logger.severe("Cannot switch runtimes when the engine has crashed");
            return;
        }
        
        if (this.errored) {
            this.logger.severe("Cannot continue as the engine has thrown an unhandled error");
            return;
        }
        
        if (this.runtime != null && this.runtime.isAlive()) {
            this.logger.severe("Cannot change runtime as it is currently in: " + this.currentRuntime.toString());
            return;
        }
        
        switch (runtime) {
            case FILE_CONNECT:         this.runtime = new LocalStartupProcess(this);
                                        break;
            
            case USER_SELECT:           this.runtime = new UserSetProcess(this);
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
        this.logger.info("Notified observers of engine changed to: " + this.currentRuntime.toString());
    }

    @Override
    public ThreadType getThreadType() {
        return ThreadType.LOCAL;
    }
    
    @Override
    public int getProgressMax() {
        return PROGRESS_MAX;
    }
    
    @Override
    public int getCurrentProgress() {
        return this.progress;
    }
    
    @Override
    public void incrementProgress() {
        if (this.progress < PROGRESS_MAX) {
            this.progress++;
        }
    }
    
    @Override
    public void sendQuitToRuntime() {
        this.logger.info("Forcing runtime shutdown of current thread, assumed reason: back was called");
        if (this.currentRuntime != RuntimeType.IDLE_LOCAL) {
            this.runtime.forceShutdown(false);
        }
    }
    
    public void runtimeFinished(boolean status) {
        this.runtime = null;
        this.currentRuntime = RuntimeType.IDLE_LOCAL;
        this.errored = status;
        this.running = false;
        
        this.setChanged();
        this.notifyObservers();
    }
    
    public void setDatabaseConnection(DatabaseConnection connection) {
        this.session = connection;
    }

    @Override
    public void quitEngine() {
        this.sendQuitToRuntime();
        this.status = false;
    }
}
