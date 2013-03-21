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

package net.jonathansmith.javadpad.filesystem;

import java.io.File;

import net.jonathansmith.javadpad.DPAD;

/**
 * FileSystem
 *
 * @author Jonathan Smith
 */
public class FileSystem{
    
    public final DPAD main;
    
    private boolean initialised = false;
    private String path = "";
    private File parentDir;
    
    public FileSystem(DPAD main) {
        this.main = main;
    }
    
    public void setup(File directory) {
        if (!directory.exists() && !directory.mkdir()) {
            this.main.getLogger().severe("Could not create database directory");
            return;
        }
        
        this.path = directory.getAbsolutePath();
        this.parentDir = directory;
        this.initialiseDatabase();
    }
    
    private void initialiseDatabase() {
        if (this.parentDir == null || this.initialised == true) {
            return;
        }
        
        boolean successful = true;
        if (!this.getPluginDirectory().exists()) {
            successful &= this.getPluginDirectory().mkdir();
        }
        
        if (!this.getExperimentDirectory().exists()) {
            successful &= this.getExperimentDirectory().mkdir();
        }
        
        if (!this.getLoadedDataDirectory().exists()) {
            successful &= this.getLoadedDataDirectory().mkdir();
        }
        
        if (!this.getProcessedDataDirectory().exists()) {
            successful &= this.getProcessedDataDirectory().mkdir();
        }
        
        if (!this.getAnalysedDataDirectory().exists()) {
            successful &= this.getAnalysedDataDirectory().mkdir();
        }
        
        if (successful) {
            this.initialised = true;
        }
    }
    
    public File getPluginDirectory() {
        return new File(this.parentDir, "Plugins");
    }
    
    public File getExperimentDirectory() {
        return new File(this.parentDir, "Experiments");
    }
    
    public File getLoadedDataDirectory() {
        return new File(this.parentDir, "Loaded");
    }
    
    public File getProcessedDataDirectory() {
        return new File(this.parentDir, "Processed");
    }
    
    public File getAnalysedDataDirectory() {
        return new File(this.parentDir, "Analysed");
    }
    
    public boolean isInitialised() {
        return this.initialised;
    }
    
    public String getAbsolutePath() {
        return this.path;
    }
}
