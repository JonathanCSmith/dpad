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
package net.jonathansmith.javadpad.server.database.recordaccess;

import org.hibernate.HibernateException;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.api.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.Server;
import net.jonathansmith.javadpad.server.database.connection.DatabaseConnection;

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
    
    public RecordsList<Record> loadAll(DatabaseConnection connection) {
        RecordsList<Record> all = new RecordsList<Record> ();
        try {
            connection.beginTransaction();
            all = this.dao.findAll(connection.getSession(), this.clazz);
            connection.commitTransaction();
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }
        
        return all;
    }
    
    public boolean save(DatabaseConnection connection, T input) {
        boolean success = false;
        try {
            connection.beginTransaction();
            input = (T) this.dao.merge(connection.getSession(), input);
            this.dao.save(connection.getSession(), input);
            connection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }
        
        return success;
    }
    
    public boolean saveNew(DatabaseConnection connection, T input) {
        boolean success = false;
        try {
            connection.beginTransaction();
            this.dao.save(connection.getSession(), input);
            connection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }
        
        return success;
    }
    
    public T findByID(DatabaseConnection connection, String uuid) {
        T out = null;
        try {
            connection.beginTransaction();
            out = (T) this.dao.findByID(connection.getSession(), this.clazz, uuid);
            connection.commitTransaction();
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }
        
        return out;
    }
    
    public boolean deleteExisting(DatabaseConnection connection, T input) {
        boolean success = false;
        try {
            connection.beginTransaction();
            this.dao.delete(connection.getSession(), input);
            connection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
            connection.rollbackTransaction();
        }
        
        return success;
    }
    
    public abstract <L extends GenericDAO> L getDAO();
}
