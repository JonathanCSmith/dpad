/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.database;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;

import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public abstract class GenericManager<T> {
    
    public final GenericDAO dao;
    private final Class<T> clazz;
    
    public GenericManager(GenericDAO dao, Class clazz) {
        this.dao = dao;
        this.clazz = clazz;
    }
    
    public List<T> loadAll() {
        List<T> all = new ArrayList<T> ();
        try {
            DatabaseConnection.beginTransaction();
            all = this.dao.findAll(this.clazz);
            DatabaseConnection.commitTransaction();
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return all;
    }
    
    public boolean saveNew(T input) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            this.dao.save(input);
            DatabaseConnection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return success;
    }
    
    public T findByID(String uuid) {
        T out = null;
        try {
            DatabaseConnection.beginTransaction();
            out = (T) this.dao.findByID(this.clazz, uuid);
            DatabaseConnection.commitTransaction();
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return out;
    }
    
    public boolean deleteExisting(T input) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            this.dao.delete(input);
            DatabaseConnection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
            DatabaseConnection.rollbackTransaction();
        }
        
        return success;
    }
    
    public abstract <L extends GenericDAO> L getDAO();
}
