/*
 * Copyright (C) 2013 Jon
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
package net.jonathansmith.javadpad.common.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;


/**
 *
 * @author Jon
 */
public class PluginManager {
    
    public static PluginManager instance;
    
    public final String path;
    
    private Framework pluginFramework;
    private BundleContext context;
    
    public PluginManager(String pluginsPath) {
        this.path = pluginsPath;
        this.setupOSGI();
        instance = this;
    }
    
    private void setupOSGI() {
        Map<String, String> config = new HashMap<String, String> ();
        config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.jonathansmith"); // TODO: Narrow what we will actually need including the packaged dependancies
        config.put(Constants.FRAMEWORK_STORAGE, this.path);
        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
        this.pluginFramework = frameworkFactory.newFramework(config);
        this.context = this.pluginFramework.getBundleContext();
    }
    
    public void listPlugins() {
        
    }
    
    public void addPlugin() {
        
    }
    
    public void removePlugin() {
        
    }
    
    public Bundle getPlugin() {
        return null;
    }
    
    public void shutdown(boolean force) {
        try {
            this.pluginFramework.waitForStop(0);
        }
        
        catch (InterruptedException ex) {
            // Don't know, we are already shutting down....
            // TODO: Think on this?
        }
    }
    
    public enum PluginType {
        LOADER,
        ANALYSER;
    }
    
    public static PluginManager getInstance() {
        return instance;
    }
}
