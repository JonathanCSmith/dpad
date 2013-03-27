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
package net.jonathansmith.javadpad.database.experiment;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;

import net.jonathansmith.javadpad.database.DatabaseConnection;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author Jon
 */
public class ExperimentManager {
    
    private static ExperimentManager instance = null;
    private ExperimentDAO experimentDAO = new ExperimentDAO();
    
    public static ExperimentManager getInstance() {
        if (instance == null) {
            instance = new ExperimentManager();
        }
        
        return instance;
    }
    
    public List<Experiment> loadExperiments() {
        List<Experiment> experiments = new ArrayList<Experiment> ();
        try {
            DatabaseConnection.beginTransaction();
            experiments = experimentDAO.findAll(Experiment.class);
            DatabaseConnection.commitTransaction();
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return experiments;
    }
    
    public boolean saveNewExperiment(Experiment experiment) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            experimentDAO.save(experiment);
            DatabaseConnection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return success;
    }
    
    public Experiment findExperimentByID(String uuid) {
        Experiment experiment = null;
        try {
            DatabaseConnection.beginTransaction();
            experiment = (Experiment) experimentDAO.findByID(Experiment.class, uuid);
            DatabaseConnection.commitTransaction();
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return experiment;
    }
    
    public boolean deleteExperiment(Experiment experiment) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            experimentDAO.delete(experiment);
            DatabaseConnection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
            DatabaseConnection.rollbackTransaction();
        }
        
        return success;
    }
}
