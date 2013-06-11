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
package net.jonathansmith.javadpad.server.database.experiment;

import net.jonathansmith.javadpad.common.database.Experiment;
import net.jonathansmith.javadpad.server.database.GenericManager;

/**
 *
 * @author Jon
 */
public class ExperimentManager extends GenericManager<Experiment> {
    
    private static ExperimentManager instance = null;
    
    private ExperimentManager() {
        super(new ExperimentDAO(), Experiment.class);
    }
    
    public static ExperimentManager getInstance() {
        if (instance == null) {
            instance = new ExperimentManager();
        }
        
        return instance;
    }
    
    @Override
    public ExperimentDAO getDAO() {
        return (ExperimentDAO) this.dao;
    }
}
