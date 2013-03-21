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
        if (!directory.exists()) {
            this.main.getLogger().severe("Somehow the directory you are working on does not exist");
            return;
        }
        
        File file = new File(directory, "DPAD");
        if (!file.exists() && !file.mkdir()) {
            this.main.getLogger().severe("Could not find or create the DPAD folder");
            return;
        }
        
        this.path = directory.getAbsolutePath();
        this.parentDir = file;
        this.initialiseDatabase();
    }
    
    private void initialiseDatabase() {
        if (this.parentDir == null || this.initialised == true) {
            return;
        }
        
        boolean successful = true;
        if (!this.getLoaderPluginDirectory().exists()) {
            successful &= this.getLoaderPluginDirectory().mkdir();
        }
        
        if (!this.getAnalyserPluginDirectory().exists()) {
            successful &= this.getAnalyserPluginDirectory().mkdir();
        }
        
        if (successful) {
            this.initialised = true;
        }
    }
    
    public File getLoaderPluginDirectory() {
        return new File(this.parentDir, "LoadPlugins");
    }
    
    public File getAnalyserPluginDirectory() {
        return new File(this.parentDir, "AnalysePlugins");
    }
    
    public boolean isInitialised() {
        return this.initialised;
    }
    
    public String getAbsolutePath() {
        return this.path;
    }
}
