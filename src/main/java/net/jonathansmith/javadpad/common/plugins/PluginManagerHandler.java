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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jon
 */
public class PluginManagerHandler extends Thread {
    
    private final String pluginDir;
    private final String updateDir;
    private final Engine engine;
    private final JSPFProperties props = new JSPFProperties();
    private final Map<String, LoaderPlugin> liveLoaders = new HashMap<String, LoaderPlugin> ();
    private final Map<String, AnalyserPlugin> liveAnalysers = new HashMap<String, AnalyserPlugin> ();
    private final Map<DPADPlugin, String> livePlugins = new HashMap<DPADPlugin, String> ();
    private final Map<String, LoaderPlugin> snapshottedLoaders = new HashMap<String, LoaderPlugin> ();
    private final Map<String, AnalyserPlugin> snapshottedAnalysers = new HashMap<String, AnalyserPlugin> ();
    private final Map<DPADPlugin, String> snapshottedPlugins = new HashMap<DPADPlugin, String> ();
    private final List<DPADPlugin> checkedOutPlugins = new LinkedList<DPADPlugin> ();
    
    private PluginManager manager;
    private PluginManagerUtil utils;
    private boolean isAlive = false;
    private boolean pendingUpdates = false;
    private boolean pluginCheckedOut = false;
    
    public PluginManagerHandler(String pluginDir, String updateDir, Engine engine) {
        this.pluginDir = pluginDir;
        this.updateDir = updateDir;
        this.engine = engine;
        
        this.props.setProperty(PluginManager.class, "cache.enabled", "true");
        this.props.setProperty(PluginManager.class, "cache.mode", "weak");
        this.props.setProperty(PluginManager.class, "cache.file", "jspf.cache");
        
        this.manager = PluginManagerFactory.createPluginManager(props);
        this.utils = new PluginManagerUtil(this.manager);
        this.loadPlugins();
        this.snapshot();
    }
    
    @Override
    public void run() {
        while (isAlive) {
            while (!pendingUpdates || pluginCheckedOut) {
                try {
                    Thread.sleep(100);
                }
                
                catch (InterruptedException ex) {
                    this.engine.forceShutdown("The main plugin manager thread was interrupted", ex);
                }
            }
            
            this.snapshot();
            this.utils.shutdown();
            this.manager.shutdown();
            this.relocatePendings();
            this.manager = PluginManagerFactory.createPluginManager(this.props);
            this.utils = new PluginManagerUtil(this.manager);
            this.loadPlugins();
            this.snapshot();
        }
        
        this.utils.shutdown();
        this.manager.shutdown();
    }
    
    public void addPluginFile(File plugin) {
        if (plugin.isFile() && plugin.getName().toLowerCase().endsWith(".jar")) {
            try {
                File newFile = new File(this.pluginDir + "\\" + plugin.getName());
                FileUtils.copyFile(plugin, newFile);
                this.pendingUpdates = true;
            }
            
            catch (IOException ex) {
                this.engine.warn("Could not update plugin: " + plugin.getName());
            }
        }
    }
    
    public RecordsList<Record> getLoaderPluginRecordList() {
        RecordsList<Record> list = new RecordsList<Record> ();
        for (LoaderPlugin plugin : this.snapshottedLoaders.values()) {
            list.add(((DPADPlugin) plugin).getPluginRecord());
        }
        
        return list;
    }
    
    public RecordsList<Record> getAnalyserPluginRecordList() {
        RecordsList<Record> list = new RecordsList<Record> ();
        for (AnalyserPlugin plugin : this.snapshottedAnalysers.values()) {
            list.add(((DPADPlugin) plugin).getPluginRecord());
        }
        
        return list;
    }
    
    public String getPluginPath(String name) {
        DPADPlugin plugin = this.getPluginWithoutCheckout(name);
        if (plugin == null) {
            return null;
        }
        
        return this.snapshottedPlugins.get(plugin);
    }
    
    public PluginRecord getPluginRecord(String name) {
        DPADPlugin plugin = this.getPluginWithoutCheckout(name);
        if (plugin == null) {
            return null;
        }
        
        return plugin.getPluginRecord();
    }
    
    public DPADPlugin getPlugin(String name) {
        DPADPlugin plugin = null;
        if (this.snapshottedLoaders.containsKey(name)) {
            plugin = this.snapshottedLoaders.get(name);
        }
        
        else if (this.snapshottedAnalysers.containsKey(name)) {
            plugin = this.snapshottedAnalysers.get(name);
        }
        
        if (plugin != null) {
            this.checkedOutPlugins.add(plugin);
            this.pluginCheckedOut = true;
            return plugin;
        }
        
        return null;
    }
    
    public void returnPlugin(DPADPlugin plugin) {
        if (this.checkedOutPlugins.contains(plugin)) {
            int first = this.checkedOutPlugins.indexOf(plugin);
            this.checkedOutPlugins.remove(first);
            
            if (this.checkedOutPlugins.isEmpty()) {
                this.pluginCheckedOut = false;
            }
        }
    }
    
    public void shutdown(boolean force) {
        if (!force && this.pendingUpdates) {
            while (this.pendingUpdates) {
                try {
                   Thread.sleep(100);
                }
                
                catch (InterruptedException ex) {
                    this.engine.warn("Error while waiting for pending updates to install");
                }
            }
        }
        
        this.isAlive = false;
    }
    
    // Use both
    private void loadPlugins() {
        this.liveLoaders.clear();
        this.liveAnalysers.clear();
        this.livePlugins.clear();
        
        File dir = new File(this.pluginDir);
        if (!dir.isDirectory()) {
            this.engine.forceShutdown("Plugin filesystem could not be validated", null);
            return;
        }
        
        this.manager.addPluginsFrom(dir.toURI());
        
        // Loader plugins
        Collection<LoaderPlugin> loaders = this.utils.getPlugins(LoaderPlugin.class);
        for (LoaderPlugin plugin : loaders) {
            this.liveLoaders.put(plugin.getPluginRecord().getName(), plugin);
            this.livePlugins.put(plugin, this.pluginDir + plugin.getPluginRecord().getName() + ".jar");
        }
        
        // Analyser plugins
        Collection<AnalyserPlugin> analysers = this.utils.getPlugins(AnalyserPlugin.class);
        for (AnalyserPlugin plugin : analysers) {
            this.liveAnalysers.put(plugin.getPluginRecord().getName(), plugin);
            this.livePlugins.put(plugin, this.pluginDir + plugin.getPluginRecord().getName() + ".jar");
        }
    }
    
    private void snapshot() {
        this.snapshottedLoaders.clear();
        this.snapshottedLoaders.putAll(this.liveLoaders);
        this.snapshottedAnalysers.clear();
        this.snapshottedAnalysers.putAll(this.liveAnalysers);
        this.snapshottedPlugins.clear();
        this.snapshottedPlugins.putAll(this.livePlugins);
    }
    
    // Use live
    private void relocatePendings() {
        File updateFolder = new File(this.updateDir);
        File[] jars = updateFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        
        for (File updatePlugin : jars) {
            try {
                File newFile = new File(this.pluginDir + "\\" + updatePlugin.getName());
                FileUtils.copyFile(updatePlugin, newFile);
                updatePlugin.delete();
            }
            
            catch (IOException ex) {
                this.engine.warn("Could not update plugin: " + updatePlugin.getName());
            }
        }
    }
    
    // Use snapshotted
    private DPADPlugin getPluginWithoutCheckout(String name) {
        if (this.snapshottedLoaders.containsKey(name)) {
            return this.snapshottedLoaders.get(name);
        }
        
        if (this.snapshottedAnalysers.containsKey(name)) {
            return this.snapshottedAnalysers.get(name);
        }
        
        return null;
    }
    
    public static byte compareVersions(PluginRecord r1, PluginRecord r2) {
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
}