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
package net.jonathansmith.javadpad.server.database;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.Server;

import org.hibernate.HibernateException;

/**
 *
 * @author jonathansmith
 */
public abstract class GenericManager<T extends Record> {
    
    public final GenericDAO dao;
    protected final Engine engine;
    private final Class<T> clazz;
    
    public GenericManager(GenericDAO dao, Class clazz) {
        this.engine = Server.getEngine();
        this.dao = dao;
        this.clazz = clazz;
    }
    
    public RecordsList<Record> loadAll() {
        RecordsList<Record> all = new RecordsList<Record> ();
        try {
            DatabaseConnection.beginTransaction();
            all = this.dao.findAll(this.clazz);
            DatabaseConnection.commitTransaction();
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }
        
        return all;
    }
    
    public boolean save(T input) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            this.dao.save(input);
            DatabaseConnection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }
        
        return success;
    }
    
    public boolean saveNew(T input) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            this.dao.save(input);
            DatabaseConnection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
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
            this.engine.error("Database access error", ex);
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
            this.engine.error("Database access error", ex);
            DatabaseConnection.rollbackTransaction();
        }
        
        return success;
    }
    
    public abstract <L extends GenericDAO> L getDAO();
}
