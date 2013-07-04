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
package net.jonathansmith.javadpad.common.database;

import net.jonathansmith.javadpad.server.database.recordaccess.GenericManager;
import net.jonathansmith.javadpad.server.database.recordaccess.experiment.ExperimentManager;
import net.jonathansmith.javadpad.server.database.recordaccess.loaderplugin.LoaderPluginManager;
import net.jonathansmith.javadpad.server.database.recordaccess.user.UserManager;

/**
 *
 * @author Jon
 */
public enum DatabaseRecord {
    // TODO:
    ANALYSER_PLUGIN(null),
    DATA_TYPE(null),
    EQUIPMENT(null),
    
    EXPERIMENT(ExperimentManager.getInstance()),
    
    // TODO:
    LOADER_DATA(null),
    
    LOADER_PLUGIN(LoaderPluginManager.getInstance()),
    
    // TODO:
    SAMPLE(null),
    TEMPLATE(null),
    TIME_COURSE_DATA(null),
    
    USER(UserManager.getInstance()),
    
    CURRENT_DATASET(null);
    
    private final GenericManager manager;
    
    private DatabaseRecord(GenericManager manager) {
        this.manager = manager;
    }
    
    public GenericManager getManager() {
        return this.manager;
    }
}