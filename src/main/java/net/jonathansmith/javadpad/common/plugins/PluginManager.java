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


import java.io.File;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import net.jonathansmith.javadpad.common.Engine;

import sun.misc.JarFilter;

/**
 *
 * @author Jon
 */
public class PluginManager {
    
    public final String path;
    
    private final Engine engine;
    private final Framework framework;
    private final BundleContext context;
    private final List<Bundle> allPlugins = new LinkedList<Bundle> ();
    private final Map<String, LoaderPlugin> cachedLoaders = new HashMap<String, LoaderPlugin> ();
    private final Map<String, AnalyserPlugin> cachedAnalysers = new HashMap<String, AnalyserPlugin> ();
    
    public PluginManager(String pluginsPath, Engine engine) {
        this.path = pluginsPath;
        this.engine = engine;
        
        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
        Map<String, String> config = this.buildOSGiConfigs();
        this.framework = frameworkFactory.newFramework(config);
        this.context = framework.getBundleContext();
        
        try {
            this.framework.start();
        } 
        
        catch (BundleException ex) {
            this.engine.forceShutdown("Plugin framework could not be started", new RuntimeException("PluginManager failure"));
        }
        
        this.loadLocalPlugins();
    }
    
    private Map<String, String> buildOSGiConfigs() {
        Map<String, String> config = new HashMap<String, String> ();
        config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "net.jonathansmith");
        return config;
    }
    
    private void loadLocalPlugins() {
        File dir = new File(this.path);
        if (!dir.isDirectory()) {
            this.engine.forceShutdown("Plugin filesystem cannot be validated", new RuntimeException("PluginManager failure"));
            return;
        }
        
        String[] jars = dir.list(new JarFilter());
        for (String pluginFileName : jars) {
            try {
                this.allPlugins.add(this.context.installBundle(this.path + "//" + pluginFileName));
            } 
            
            catch (BundleException ex) {
                this.engine.warn("Could not load: " + pluginFileName + " does it implement bundle activator?");
            }
        }
        
        List<Bundle> confirmedPlugins = new LinkedList<Bundle> ();
        for (Bundle plugin : this.allPlugins) {
            if (!(plugin instanceof Plugin)) {
                try {
                    plugin.uninstall();
                } 
                
                catch (BundleException ex) {
                    this.engine.warn("Could not load: " + plugin.getSymbolicName() + " as it does not implement Plugin");
                }
            }
            
            else {
                Plugin p = (Plugin) plugin;
                String name = p.getPluginRecord().getName();
                
                if (p instanceof LoaderPlugin) {
                    this.cachedLoaders.put(name, (LoaderPlugin) p);
                }
                
                else if (p instanceof AnalyserPlugin) {
                    this.cachedAnalysers.put(name, (AnalyserPlugin) p);
                }
                
                else {
                    this.engine.warn("Could not load: " + name + " as it does not conform to our two known plugin types");
                    continue;
                }
                    
                confirmedPlugins.add(plugin);
            }
        }
        
        this.allPlugins.clear();
        this.allPlugins.addAll(confirmedPlugins);
    }
}
