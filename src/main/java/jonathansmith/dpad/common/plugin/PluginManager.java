package jonathansmith.dpad.common.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.apache.commons.io.FileUtils;

import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.database.AnalysingPluginRecord;
import jonathansmith.dpad.api.database.LoadingPluginRecord;
import jonathansmith.dpad.api.plugins.IAnalyserPlugin;
import jonathansmith.dpad.api.plugins.ILoaderPlugin;
import jonathansmith.dpad.api.plugins.IPlugin;
import jonathansmith.dpad.api.plugins.records.IPluginRecord;

import jonathansmith.dpad.common.database.util.RecordList;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Core thread for loading and distributing plugins.
 * <p/>
 * TODO: Separate into client and server.
 * TODO: Allow server to acquire connection to the database
 */
public class PluginManager extends Thread {

    private final Map<IPlugin, String>         live_plugins                  = new HashMap<IPlugin, String>();
    private final Map<IPlugin, String>         snapshotted_plugins           = new HashMap<IPlugin, String>();
    private final Map<String, ILoaderPlugin>   live_loading_plugins          = new HashMap<String, ILoaderPlugin>();
    private final Map<String, ILoaderPlugin>   snapshotted_loading_plugins   = new HashMap<String, ILoaderPlugin>();
    private final Map<String, IAnalyserPlugin> live_analysing_plugins        = new HashMap<String, IAnalyserPlugin>();
    private final Map<String, IAnalyserPlugin> snapshotted_analysing_plugins = new HashMap<String, IAnalyserPlugin>();
    private final List<IPlugin>                in_use_plugins                = new LinkedList<IPlugin>();
    private final JSPFProperties               plugin_manager_properties     = new JSPFProperties();

    private final String  plugin_directory;
    private final String  update_directory;
    private final IEngine engine;

    private boolean hasError       = false;
    private boolean isAlive        = false;
    private boolean pendingUpdates = false;
    private boolean pluginInUse    = false;

    private net.xeoh.plugins.base.PluginManager manager;
    private PluginManagerUtil                   utilities;

    public PluginManager(String pluginDir, String updateDir, IEngine engine) {
        this.plugin_directory = pluginDir;
        this.update_directory = updateDir;
        this.engine = engine;

        this.plugin_manager_properties.setProperty(net.xeoh.plugins.base.PluginManager.class, "cache.enabled", "true");
        this.plugin_manager_properties.setProperty(net.xeoh.plugins.base.PluginManager.class, "cache.mode", "weak");
        this.plugin_manager_properties.setProperty(net.xeoh.plugins.base.PluginManager.class, "cache.file", "jspf.cache");

        this.manager = PluginManagerFactory.createPluginManager(this.plugin_manager_properties);
        this.utilities = new PluginManagerUtil(this.manager);
        this.loadPlugins();
        this.snapshot();
    }

    /**
     * Should be called only when a plugin is required to be added to the local plugin database.
     * All entries are entered into pending. Note: No sanity checking is performed here.
     * But sanity checking does occur as part of the process, so if the provided plugin is outdated it will not be added.
     *
     * @param plugin the plugin to add.
     */
    public void addPluginFile(File plugin) {
        if (plugin.isFile() && plugin.getName().toLowerCase().endsWith(".jar")) {
            try {
                File newFile = new File(this.update_directory + "\\" + plugin.getName());
                FileUtils.copyFile(plugin, newFile);
                this.pendingUpdates = true;
            }

            catch (IOException ex) {
                this.engine.error("Could not add plugin: " + plugin.getName(), ex);
            }
        }
    }

    public RecordList<LoadingPluginRecord> getLoaderPluginRecordList() {
        RecordList<LoadingPluginRecord> list = new RecordList<LoadingPluginRecord>();
        for (ILoaderPlugin plugin : this.snapshotted_loading_plugins.values()) {
            LoadingPluginRecord record = new LoadingPluginRecord();
            record.buildRecordFromPlugin(plugin);
            list.add(record);
        }

        return list;
    }

    public RecordList<AnalysingPluginRecord> getAnalyserPluginRecordList() {
        RecordList<AnalysingPluginRecord> list = new RecordList<AnalysingPluginRecord>();
        for (IAnalyserPlugin plugin : this.snapshotted_analysing_plugins.values()) {
            AnalysingPluginRecord record = new AnalysingPluginRecord();
            record.buildRecordFromPlugin(plugin);
            list.add(record);
        }

        return list;
    }

    public String getPluginPath(String name) {
        IPlugin plugin = this.getPluginWithoutCheckout(name);
        if (plugin == null) {
            return null;
        }

        return this.snapshotted_plugins.get(plugin);
    }

    public IPluginRecord getPluginRecord(String name) {
        IPlugin plugin = this.getPluginWithoutCheckout(name);
        if (plugin == null) {
            return null;
        }

        return plugin.getPluginRecord();
    }

    public IPlugin getPlugin(String name) {
        IPlugin plugin = null;
        if (this.snapshotted_loading_plugins.containsKey(name)) {
            plugin = this.snapshotted_loading_plugins.get(name);
        }

        else if (this.snapshotted_analysing_plugins.containsKey(name)) {
            plugin = this.snapshotted_analysing_plugins.get(name);
        }

        if (plugin != null) {
            this.in_use_plugins.add(plugin);
            this.pluginInUse = true;
            return plugin;
        }

        return null;
    }

    public void returnPlugin(IPlugin plugin) {
        int index = this.in_use_plugins.indexOf(plugin);
        if (index == -1) {
            return;
        }

        this.in_use_plugins.remove(index);
        if (this.in_use_plugins.isEmpty()) {
            this.pluginInUse = false;
        }
    }

    // TODO: Should we be able to do this externally?!
    public void markPendingUpdates() {
        this.pendingUpdates = true;
    }

    public void shutdown(boolean force) {
        if (!force && this.pendingUpdates) {
            while (this.pendingUpdates) {
                try {
                    Thread.sleep(100);
                }

                catch (InterruptedException ex) {
                    // REVISIT
                }
            }
        }

        this.isAlive = false;
    }

    @Override
    public void start() {
        if (!this.hasError) {
            this.isAlive = true;
        }

        super.start();
    }

    @Override
    public void run() {
        while (this.isAlive) {
            if (!this.pendingUpdates || this.pluginInUse) {
                try {
                    Thread.sleep(100);
                    continue;
                }

                catch (InterruptedException ex) {
                    // REVIST
                }
            }

            this.snapshot();
            this.utilities.shutdown();
            this.manager.shutdown();
            this.relocatePendingPlugins();
            this.manager = PluginManagerFactory.createPluginManager(this.plugin_manager_properties);
            this.utilities = new PluginManagerUtil(this.manager);
            this.loadPlugins();
            this.snapshot();
        }

        this.utilities.shutdown();
        this.manager.shutdown();
    }

    /**
     * Sanity checking for plugin files needs to happen here
     */
    private void relocatePendingPlugins() {
        File updateFolder = new File(this.update_directory);
        File[] pluginJars = updateFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });

        boolean successful = true;
        for (File updatePlugin : pluginJars) {
            try {
                File relocatedJar = new File(this.plugin_directory + "\\" + updatePlugin.getName());
                FileUtils.copyFile(updatePlugin, relocatedJar);
                updatePlugin.delete();
            }

            catch (IOException ex) {
                successful = false;
                this.engine.error("Could not update plugin: " + updatePlugin.getName(), ex);
            }
        }

        if (successful) {
            this.pendingUpdates = false;
        }
    }

    // TODO: Database submission
    private void loadPlugins() {
        this.live_loading_plugins.clear();
        this.live_analysing_plugins.clear();
        this.live_plugins.clear();

        File dir = new File(this.plugin_directory);
        if (!dir.isDirectory()) {
            this.engine.handleError("Could not load plugins from the provided plugin directory. Are you sure the FileSystem was setup correctly?", null);
            this.hasError = true;
            return;
        }

        try {
            this.manager.addPluginsFrom(new URI("classpath://jonathansmith.**"));
        }

        catch (Exception e) {
            this.engine.handleError("Error loading plugins from the classpath", e);
        }
        this.manager.addPluginsFrom(dir.toURI());

        // Loading plugins
        Collection<ILoaderPlugin> loaders = this.utilities.getPlugins(ILoaderPlugin.class);
        for (ILoaderPlugin plugin : loaders) {
            this.live_loading_plugins.put(plugin.getPluginRecord().getPluginName(), plugin);
            this.live_plugins.put(plugin, this.plugin_directory + "\\" + plugin.getPluginRecord().getPluginName() + ".jar");
        }

        // Analyser plugins
        Collection<IAnalyserPlugin> analysers = this.utilities.getPlugins(IAnalyserPlugin.class);
        for (IAnalyserPlugin plugin : analysers) {
            this.live_analysing_plugins.put(plugin.getPluginRecord().getPluginName(), plugin);
            this.live_plugins.put(plugin, this.plugin_directory + "\\" + plugin.getPluginRecord().getPluginName() + ".jar");
        }
    }

    private void snapshot() {
        this.snapshotted_loading_plugins.clear();
        this.snapshotted_loading_plugins.putAll(this.live_loading_plugins);
        this.snapshotted_analysing_plugins.clear();
        this.snapshotted_analysing_plugins.putAll(this.live_analysing_plugins);
        this.snapshotted_plugins.clear();
        this.snapshotted_plugins.putAll(this.live_plugins);
    }

    private IPlugin getPluginWithoutCheckout(String name) {
        if (this.snapshotted_loading_plugins.containsKey(name)) {
            return this.snapshotted_loading_plugins.get(name);
        }

        if (this.snapshotted_analysing_plugins.containsKey(name)) {
            return this.snapshotted_analysing_plugins.get(name);
        }

        return null;
    }
}
