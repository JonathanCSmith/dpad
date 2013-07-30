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
package net.jonathansmith.javadpad.api.plugin;

import net.xeoh.plugins.base.Plugin;

import net.jonathansmith.javadpad.api.Platform;
import net.jonathansmith.javadpad.api.database.PluginRecord;
import net.jonathansmith.javadpad.api.threads.IRuntime;
import net.jonathansmith.javadpad.api.utils.ILogger;

/**
 *
 * @author Jon
 */
public interface IPlugin extends Plugin {
    
    public PluginRecord getPluginRecord();
    
    public IRuntime getRuntimeThread(Platform platform, ILogger logger);
}
