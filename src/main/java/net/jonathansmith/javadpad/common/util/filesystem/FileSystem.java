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

package net.jonathansmith.javadpad.common.util.filesystem;

import java.io.File;

import java.net.URISyntaxException;

import net.jonathansmith.javadpad.api.Platform;
import net.jonathansmith.javadpad.common.Engine;

/**
 * FileSystem
 *
 * @author Jonathan Smith
 */
public class FileSystem {
    
    public final Engine engine;
    
    private File rootDirectory;
    private File parentDir;
    private String path = "";
    
    public FileSystem(Engine engine) {
        this.engine = engine;
    }
    
    public void init() {
        try {
            String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File file = new File(classPath);
            if (classPath.contains(".jar")) {
                file = file.getParentFile();
            }
            
            this.rootDirectory = file;
            this.setup();
        } catch (URISyntaxException ex) {
            this.engine.forceShutdown("Could not initialise file system, exiting", ex);
        }
    }
    
    private void setup() throws URISyntaxException {
        if (!this.rootDirectory.exists()) {
            throw new URISyntaxException("Could not locate root directory", this.rootDirectory.getAbsolutePath());
        }
        
        File file = new File(this.rootDirectory, "DPAD");
        if (!file.exists() && !file.mkdir()) {
            throw new URISyntaxException("Could not build parent directory", file.getAbsolutePath());
        }
        
        File main = new File(file, this.engine.platform.toString().toLowerCase());
        if (!main.exists() && !main.mkdir()) {
            throw new URISyntaxException("Could not build platform specific directory", main.getAbsolutePath());
        }
        
        this.parentDir = main;
        this.path = main.getAbsolutePath();
        this.buildFileStructure();
    }
    
    private void buildFileStructure() throws URISyntaxException{
        boolean successful = true;
        if (successful && !this.getLogDirectory().exists()) {
            successful &= this.getLogDirectory().mkdir();
        }
        
        if (successful && this.engine.platform == Platform.SERVER && !this.getDatabaseDirectory().exists()) {
            successful &= this.getDatabaseDirectory().mkdir();
        }
        
        if (successful && !this.getPluginDirectory().exists()) {
            successful &= this.getPluginDirectory().mkdir();
        }
        
        if (successful && !this.getUpdateDirectory().exists()) {
            successful &= this.getUpdateDirectory().mkdir();
        }
        
        if (!successful) {
            throw new URISyntaxException("Could not build file structure", "ALL");
        }
    }
    
    public File getLogDirectory() {
        return new File(this.parentDir, "Logs");
    }
    
    public File getDatabaseDirectory() {
        if (this.engine.platform != Platform.SERVER) {
            return null;
        }
        
        return new File(this.parentDir, "Database");
    }

    public File getUpdateDirectory() {
        return new File(this.parentDir, "Updates");
    }
    
    public File getPluginDirectory() {
        return new File(this.parentDir, "Plugins");
    }
    
    public String getParentAbsolutePath() {
        return this.path;
    }
}
