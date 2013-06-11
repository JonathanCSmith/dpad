/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.common;

import org.slf4j.Logger;

import net.jonathansmith.javadpad.DPAD;
import net.jonathansmith.javadpad.DPAD.Platform;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;
import net.jonathansmith.javadpad.common.util.filesystem.FileSystem;
import net.jonathansmith.javadpad.common.util.threads.RuntimeThread;

/**
 *
 * @author jonathansmith
 */
public abstract class Engine extends Thread {
    
    public final DPAD main;
    public final Platform platform;
    
    private static Engine instance;
    private final String version = "0.0.1";
    
    public String hostName;
    public int portNumber;
    public TabbedGUI gui = null;
    
    protected boolean isAlive = false;
    protected boolean errored = false;
    
    private Logger logger = null;
    private FileSystem fileSystem = null;
    private boolean loggerIsSetup;
    
    public Engine(DPAD main, Platform platform, String hostName, int portNumber) {
        this.main = main;
        this.platform = platform;
        this.hostName = hostName;
        this.portNumber = portNumber;
        
        instance = this;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public boolean isDebug() {
        return this.main.debug;
    }
    
    public TabbedGUI getGUI() {
        return this.gui;
    }
    
    public void setGUI(TabbedGUI gui) {
        if (this.gui != null) {
            this.warn("Cannot set gui once it has been established");
            return;
        }
        
        this.gui = gui;
        this.main.acquireTab(this.platform, this.gui);
    }
    
    public FileSystem getFileSystem() {
        return this.fileSystem;
    }
    
    public void setFileSystem(FileSystem fileSystem) {
        if (this.fileSystem != null) {
            this.warn("Cannot set fileSystem once it has been established");
            return;
        }
        
        this.fileSystem = fileSystem;
    }
    
    public boolean isLoggerSetup() {
        return this.loggerIsSetup;
    }
    
    public void setCompleteLogger(Logger logger) {
        if (this.logger != null) {
            this.warn("Cannot set logger once it has been established");
            return;
        }
        
        this.logger = logger;
        this.loggerIsSetup = true;
    }
    
    public void init() {
        this.gui.init();
        this.gui.run();
        this.fileSystem.init();
    }
    
    public boolean isRunning() {
        return this.isAlive;
    }
    
    public boolean isViable() {
        return !this.errored;
    }
    
    public abstract void setRuntime(RuntimeThread thread);
    
    public abstract void sendQuitToRuntimeThread(String message, boolean error);
    
    public abstract void saveAndShutdown();
    
    public abstract void forceShutdown(String cause, Throwable ex);
    
    public void trace(String message) {
        if (this.isLoggerSetup()) {
            this.logger.trace(message);
        }
    }
    
    public void trace(String message, Throwable ex) {
        if (this.isLoggerSetup()) {
            this.logger.trace(message, ex);
        }
    }
    
    // Equivalent of fine
    public void debug(String message) {
        if (this.isLoggerSetup()) {
            this.logger.debug(message);
        }
    }
    public void debug(String message, Throwable ex) {
        if (this.isLoggerSetup()) {
            this.logger.debug(message, ex);
        }
    }
    
    // Equivalent of info
    public void info(String message) {
        if (this.isLoggerSetup()) {
            this.logger.info(message);
        }
    }
    
    public void info(String message, Throwable ex) {
        if (this.isLoggerSetup()) {
            this.logger.info(message, ex);
        }
    }
    
    // Equivalent of warning
    public void warn(String message) {
        if (this.isLoggerSetup()) {
            this.logger.warn(message);
        }
        
        else {
            System.out.println(message);
        }
    }
    
    public void warn(String message, Throwable ex) {
        if (this.isLoggerSetup()) {
            this.logger.warn(message, ex);
        }
        
        else {
            System.out.println(message);
            ex.printStackTrace();
        }
    }
    
    // Equivalent of severe
    public void error(String message) {
        if (this.isLoggerSetup()) {
            this.logger.error(message);
        }
        
        else {
            System.out.println(message);
        }
    }
    
    public void error(String message, Throwable ex) {
        if (this.isLoggerSetup()) {
            this.logger.error(message, ex);
        }
        
        else {
            System.out.println(message);
            ex.printStackTrace();
        }
    }
    
    public static Engine getEngine() {
        return instance;
    }
}
