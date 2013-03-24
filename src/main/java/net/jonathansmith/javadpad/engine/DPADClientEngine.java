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

import net.jonathansmith.javadpad.database.entry.ExperimentEntry;
import net.jonathansmith.javadpad.database.entry.UserEntry;
import net.jonathansmith.javadpad.util.FileSystem;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author Jon
 */
public abstract class DPADClientEngine extends DPADEngine {

    public UserEntry user = null;
    public ExperimentEntry experiment = null;
    
    public DPADClientEngine(DPADLogger logger, FileSystem fileSystem) {
        super(logger, fileSystem);
    }
    
    public UserEntry getUser() {
        return this.user;
    }
    
    public ExperimentEntry getExperiment() {
        return this.experiment;
    }
}
