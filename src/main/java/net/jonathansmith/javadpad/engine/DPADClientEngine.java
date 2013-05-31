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
package net.jonathansmith.javadpad.engine;

import net.jonathansmith.javadpad.database.batch.Batch;
import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.plugin.DPADPluginManager;
import net.jonathansmith.javadpad.util.FileSystem;

/**
 *
 * @author Jon
 */
public abstract class DPADClientEngine extends DPADEngine {

    public User user = null;
    public Experiment experiment = null;
    public Batch batch = null;
    
    private DPADPluginManager pluginManager = null;
    
    public DPADClientEngine(FileSystem fileSystem) {
        super(fileSystem);
    }
    
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Experiment getExperiment() {
        return this.experiment;
    }
    
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }
    
    public Batch getBatch() {
        return this.batch;
    }
    
    public void setBatch(Batch batch) {
        this.batch = batch;
    }
    
    public DPADPluginManager getLocalPluginManager() {
        return this.pluginManager;
    }
    
    public void setLocalPluginManager(DPADPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }
}
