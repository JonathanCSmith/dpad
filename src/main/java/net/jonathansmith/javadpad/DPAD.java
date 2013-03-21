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
package net.jonathansmith.javadpad;

import java.io.File;

import java.awt.EventQueue;

import javax.swing.UIManager;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.filesystem.FileSystem;
import net.jonathansmith.javadpad.gui.DPADGui;
import net.jonathansmith.javadpad.util.DPADLogger;

/**
 *
 * @author Jonathan Smith
 */
public class DPAD extends Thread {
    
    public static DPAD instance;
    
    public final DPADLogger mainLogger;
    
    public boolean initialised = false;
    public boolean errored = false;
    
    private DPADEngine engine;
    private DPADGui gui;
    private DPADController controller;
    private FileSystem database;
    
    /**
     * @param args the command line arguments
     */
    @SuppressWarnings({"CallToThreadDumpStack", "SleepWhileInLoop"})
    public static void main(String[] args) {
        DPAD main = new DPAD();
        main.init();
        
        if (main.initialised) {
            System.out.println("Attempting to start main threads");
            
            main.start();
            
            while (main.isAlive()) {
                try {
                    Thread.sleep(1000);
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.out.println("Main thread interrupted");
                }
            }
            
            if (main.errored) {
                System.out.println("Runtime failure in DPAD");
            }
            
        } else {
            System.out.println("Failed to successfully initialise DPAD");
        }
        
        Runtime.getRuntime().halt(1);
    }
    
    public DPAD() {
        this.mainLogger = new DPADLogger();
        this.engine = new DPADEngine(this.mainLogger);
        this.gui = new DPADGui(this.mainLogger, this.engine);
        this.controller = new DPADController(this.mainLogger, this.engine, this.gui);
        this.database = new FileSystem(this);
    }
    
    public static DPAD getInstance() {
        if (instance == null) {
            instance = new DPAD();
        }
        
        return instance;
    }
    
    @SuppressWarnings("CallToThreadDumpStack")
    private void init() {
        try {
            File file = this.gui.getFileSystem();
            if (file == null) {
                this.mainLogger.severe("User failed to provide directory");
                return;
            }
            
            this.database.setup(file);
            if (!this.database.isInitialised()) {
                this.mainLogger.severe("Failure to validate database");
                return;
                
            } else {
                this.mainLogger.info("Database successfully setup at: " + this.database.getAbsolutePath());
            }
            
            this.engine.init();
            this.gui.init();
            this.controller.init();
            
            this.initialised = true;
            
        } catch (Throwable t) {
            this.mainLogger.severe("Exception in initialisation, shutting down ...");
            t.printStackTrace();
        }
    }
    
    @SuppressWarnings({"CallToThreadDumpStack", "SleepWhileInLoop", "UseSpecificCatch"})
    @Override
    public void run() {
        try {
            if (!this.initialised) {
                this.mainLogger.severe("Not initialised yet.. cannot run.");
                throw new RuntimeException();
            }
            
            EventQueue.invokeLater(this.gui);
            this.engine.run();
            this.controller.run();
            
            this.mainLogger.info("Threads started: idling in main");
            
            while (!this.gui.errored || !this.engine.errored || !this.controller.errored || !this.errored) {
                Thread.sleep(10);
                
                if (!this.gui.isShowing()) {
                    this.errored = true;
                }
            }
            
        } catch (Throwable t) {
            this.mainLogger.severe("Exception in runtime, shutting down ...");
            t.printStackTrace();
            this.errored = true;
        }
    }
    
    public FileSystem getFileSystem() {
        return this.database;
    }
    
    public DPADLogger getLogger() {
        return mainLogger;
    }
}
