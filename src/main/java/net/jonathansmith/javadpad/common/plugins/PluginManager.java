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
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jon
 */
public class PluginManager {
    
    public final String path;
    
    private final Engine engine;
    private final Framework framework;
    private final BundleContext context;
    private final Map<Bundle, String> allPlugins = new HashMap<Bundle, String> ();
    private final Map<String, Bundle> cachedLoaders = new HashMap<String, Bundle> ();
    private final Map<String, Bundle> cachedAnalysers = new HashMap<String, Bundle> ();
    
    public PluginManager(String pluginsPath, Engine engine) {
        this.path = pluginsPath;
        this.engine = engine;
        
        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
        Map<String, String> config = this.buildOSGiConfigs();
        this.framework = frameworkFactory.newFramework(config);
        
        try {
            this.framework.start();
        } 
        
        catch (BundleException ex) {
            this.engine.forceShutdown("Plugin framework could not be started", new RuntimeException("PluginManager failure"));
        }
        
        this.context = framework.getBundleContext();
        this.loadLocalPlugins();
    }
    
    private Map<String, String> buildOSGiConfigs() {
        Map<String, String> config = new HashMap<String, String> ();
        config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "net.jonathansmith.javadpad");
        config.put(Constants.FRAMEWORK_STORAGE, this.path);
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
        return config;
    }
    
    private void loadLocalPlugins() {
        File dir = new File(this.path);
        if (!dir.isDirectory()) {
            this.engine.forceShutdown("Plugin filesystem cannot be validated", new RuntimeException("PluginManager failure"));
            return;
        }
        
        String[] jars = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.toLowerCase().endsWith(".zip") ? name.toLowerCase().endsWith(".jar") : true;
            }
        });
        
        for (String pluginFileName : jars) {
            try {
                this.allPlugins.put(this.context.installBundle(this.path + "//" + pluginFileName), this.path + "//" + pluginFileName);
            } 
            
            catch (BundleException ex) {
                this.engine.warn("Could not load: " + pluginFileName + " does it implement bundle activator?");
            }
        }
        
        Map<Bundle, String> confirmedPlugins = new HashMap<Bundle, String> ();
        for (Bundle plugin : this.allPlugins.keySet()) {
            if (!(plugin instanceof LoaderPlugin) && !(plugin instanceof AnalyserPlugin)) {
                try {
                    plugin.uninstall();
                } 
                
                catch (BundleException ex) {
                    this.engine.warn("Could not load: " + plugin.getSymbolicName() + " as it does not implement a known derivative of plugin");
                    continue;
                }
            }
            
            else {
                Plugin p = (Plugin) plugin;
                String name = p.getPluginRecord().getName();
                
                if (p instanceof LoaderPlugin) {
                    // TODO:
                    // 1) If contains name already
                    // 2) Compare versions
                    // 3) Load only highest
                    // 4) Flag older for deletion?
                    
                    this.cachedLoaders.put(name, plugin);
                }
                
                else if (p instanceof AnalyserPlugin) {
                    // TODO:
                    // 1) If contains name already
                    // 2) Compare versions
                    // 3) Load only highest
                    // 4) Flag older for deletion?
                    
                    this.cachedAnalysers.put(name, plugin);
                }
                    
                confirmedPlugins.put(plugin, this.allPlugins.get(plugin));
            }
        }
        
        this.allPlugins.clear();
        this.allPlugins.putAll(confirmedPlugins);
    }
    
    public RecordsList<Record> getLocalPluginRecordList() {
        RecordsList<Record> list = new RecordsList<Record> ();
        for (Bundle bundle : this.allPlugins.keySet()) {
            list.add(((Plugin) bundle).getPluginRecord());
        }
        
        return list;
    }
    
    public String getPluginPath(String name) {
        Bundle b = this.getBundle(name);
        if (b == null) {
            return null;
        }
        
        return this.allPlugins.get(b);
    }
    
    public PluginRecord getLocalPluginRecord(String name) {
        Bundle b = this.getBundle(name);
        if (b == null) {
            return null;
        }
        
        return ((Plugin) b).getPluginRecord();
    }
    
    public Bundle getBundle(String name) {
        Bundle b;
        if (this.cachedLoaders.containsKey(name)) {
            b = this.cachedLoaders.get(name);
        }
        
        else if (this.cachedAnalysers.containsKey(name)) {
            b = this.cachedAnalysers.get(name);
        }
        
        else {
            return null;
        }
        
        return b;
    }
    
    public byte compareVersions(PluginRecord r1, PluginRecord r2) {
        String[] versionSet1 = r1.getVersion().split(".");
        int[] versionValues1 = new int[versionSet1.length];
        for (int i = 0; i < versionSet1.length; i++) {
            versionValues1[i] = Integer.parseInt(versionSet1[i]);
        }
        
        String[] versionSet2 = r2.getVersion().split(".");
        int[] versionValues2 = new int[versionSet2.length];
        for (int i = 0; i < versionSet2.length; i++) {
            versionValues2[i] = Integer.parseInt(versionSet2[i]);
        }
        
        if (versionValues1.length > versionValues2.length) {
            for (int i = 0; i < versionValues1.length; i++) {
                if (versionValues1[i] > versionValues2[i]) {
                    return -1;
                }
                
                else if (versionValues2[i] > versionValues1[i]) {
                    return 1;
                }
            }
            
            return -1;
        }
        
        else if (versionValues2.length > versionValues1.length) {
            for (int i = 0; i < versionValues2.length; i++) {
                if (versionValues2[i] > versionValues1[i]) {
                    return 1;
                }
                
                else if (versionValues1[i] > versionValues2[i]) {
                    return -1;
                }
            }
            
            return 1;
        }
        
        else {
            for (int i = 0; i < versionValues1.length; i++) {
                if (versionValues1[i] > versionValues2[i]) {
                    return -1;
                }
                
                else if (versionValues2[i] > versionValues1[i]) {
                    return 1;
                }
            }
            
            return 0;
        }
    }
    
    public void addOrUpdatePlugin(String name, String currentPath, boolean shouldClear) {
        Bundle b = this.getBundle(name);
        File currentFile = new File(currentPath);
        if (!currentFile.exists() || !currentFile.isFile()) {
            return;
        }
        
        if (b != null) {
            try {
                b.uninstall();
            } 
            
            catch (BundleException ex) {
                this.engine.warn("Could not uninstal existing plugin: " + b.getSymbolicName());
            }
        }
        
        try {
            File newFile = new File(this.path + "\\" + name + ".jar");
            FileUtils.copyFile(currentFile, newFile);
            Bundle nb = this.context.installBundle("file:" + newFile.getAbsolutePath());
            this.allPlugins.put(nb, newFile.getAbsolutePath());
            
            if (nb instanceof LoaderPlugin) {
                LoaderPlugin l = (LoaderPlugin) nb;
                this.cachedLoaders.put(l.getPluginRecord().getName(), nb);
            }
            
            else if (nb instanceof AnalyserPlugin) {
                AnalyserPlugin a = (AnalyserPlugin) nb;
                this.cachedAnalysers.put(a.getPluginRecord().getName(), nb);
            }
        } 

        catch (BundleException ex) {
            this.engine.warn("Could not install plugin: " + b.getSymbolicName(), ex);
        } 

        catch (IOException ex) {
            this.engine.warn("Could not copy plugin: " + b.getSymbolicName(), ex);
        }
        
        if (shouldClear) {
            currentFile.delete();
        }
    }
}
