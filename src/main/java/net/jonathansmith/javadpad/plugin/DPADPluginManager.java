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
package net.jonathansmith.javadpad.plugin;

import net.jonathansmith.javadpad.util.FileSystem;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;

/**
 *
 * @author Jon
 */
public class DPADPluginManager {
    
    public PluginManager pluginManager;
    public PluginManagerUtil utils;
    public final JSPFProperties props;
    public FileSystem fileSystem;
    
    public DPADPluginManager() {
        this.props = new JSPFProperties();
        this.props.setProperty(PluginManager.class, "cache.enabled", "true");
        this.props.setProperty(PluginManager.class, "cache.mode",    "weak"); //optional
        this.props.setProperty(PluginManager.class, "cache.file",    "jspf.cache");
        
        this.pluginManager = PluginManagerFactory.createPluginManager(this.props);
        this.utils = new PluginManagerUtil(this.pluginManager);
    }
    
    public void setFileSystemAndSetup(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.pluginManager.addPluginsFrom(this.fileSystem.getLoaderPluginDirectory().toURI());
        this.pluginManager.addPluginsFrom(this.fileSystem.getAnalyserPluginDirectory().toURI());
    }
}
