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

package net.jonathansmith.javadpad.controller;

import java.io.File;
import java.io.PrintStream;

import java.net.URISyntaxException;

import java.util.logging.Level;
import java.awt.EventQueue;

import net.jonathansmith.javadpad.controller.listener.ClientMainPanelListener;
import net.jonathansmith.javadpad.controller.listener.ExperimentPanelListener;
import net.jonathansmith.javadpad.controller.listener.StartupPanelListener;
import net.jonathansmith.javadpad.controller.listener.UserPanelListener;
import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.engine.DPADClientEngine;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.host.DPADHostEngine;
import net.jonathansmith.javadpad.util.FileSystem;
import net.jonathansmith.javadpad.gui.DPADGui;
import net.jonathansmith.javadpad.util.logging.DPADLogger;
import net.jonathansmith.javadpad.util.ThreadType;
import net.jonathansmith.javadpad.util.logging.LoggerOutputStream;

/**
 * DPADController
 *
 * @author Jonathan Smith
 */
public class DPADController extends Thread {
    
    public final DPADLogger logger;
    public final DPADGui gui;
    public final FileSystem fileSystem;
    
    public boolean errored = false;
    public boolean initialised = false;
    public DPADEngine engine = null;
    
    public DPADController() {
        this.logger = new DPADLogger();
        this.gui = new DPADGui(this);
        this.fileSystem = new FileSystem(this);
    }
    
    public void init() {
        //System.setOut(new PrintStream(new LoggerOutputStream(this.logger, Level.OFF), true));
        //System.setErr(new PrintStream(new LoggerOutputStream(this.logger, Level.OFF), true));
        
        this.gui.init();
        EventQueue.invokeLater(this.gui);
        
        this.buildLocalFileSystem();
        this.gui.addStartupSelectListener(new StartupPanelListener(this));
        this.initialised = true;
    }
    
    @Override
    public void run() {
        try {
            this.logger.info("Threads started, idling in main");
            while (!this.errored || !this.gui.isShowing()) {
                Thread.sleep(100);
                
                // Should do error checking and handling here, specific to each
                // child thread
            }
            
        } catch (InterruptedException ex) {
            this.logger.severe("Main thread interrupted, program will exit");
        }
    }

    public DPADGui getGui() {
        return this.gui;
    }

    public DPADEngine getEngine() {
        return this.engine;
    }
    
    public void setEngine(DPADEngine eng) {
        if (this.engine != null || eng == null) {
            this.logger.warning("Cannot change the DPAD engine once it has been set");
            return;
        }
        
        this.addListeners(eng.getThreadType());
        this.engine = eng;
        this.gui.setEngine(eng);
        this.engine.init();
        (new Thread(this.engine)).start();
    }
    
    public User getSessionUser() {
        if (this.engine instanceof DPADHostEngine) {
            return null;
        }
        
        DPADClientEngine client = (DPADClientEngine) this.engine;
        return client.getUser();
    }
    
    public void setSessionUser(User user) {
        if (!(this.engine instanceof DPADHostEngine)) {
            DPADClientEngine client = (DPADClientEngine) this.engine;
            client.setUser(user);
        }
    }
    
    public Experiment getSessionExperiment() {
        if (this.engine instanceof DPADHostEngine) {
            return null;
        }
        
        DPADClientEngine client = (DPADClientEngine) this.engine;
        return client.getExperiment();
    }
    
    public void setSessionExperiment(Experiment experiment) {
        if (!(this.engine instanceof DPADHostEngine)) {
            DPADClientEngine client = (DPADClientEngine) this.engine;
            client.setExperiment(experiment);
        }
    }
    
    private void addListeners(ThreadType type) {
        switch (type) {
            case LOCAL:             this.gui.addMainMenuListener(new ClientMainPanelListener(this));
                                    this.gui.addUserRuntimeListener(new UserPanelListener(this));
                                    this.gui.addExperimentRuntimeListener(new ExperimentPanelListener(this));
                                    break;
                
            case CLIENT:            this.gui.addMainMenuListener(new ClientMainPanelListener(this));
                                    this.gui.addUserRuntimeListener(new UserPanelListener(this));
                                    this.gui.addExperimentRuntimeListener(new ExperimentPanelListener(this));
                                    break;
                
            case HOST:              break;
        }
    }
    
    private void buildLocalFileSystem() {
        try {
            String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File file = new File(classPath);
            if (classPath.contains(".jar")) {
                file = file.getParentFile();
            }
            
            this.fileSystem.setup(file);
            if (!this.fileSystem.isSetup()) {
                this.logger.severe("Failure to validate filesystem, program will exit");
                
            } else {
                this.logger.info("File system successfully setup at: " + this.fileSystem.getAbsolutePath());
            }
        } catch (URISyntaxException ex) {
            this.logger.severe("Could not retrieve path URI, DPAD likely to exit");
        }
    }
}
