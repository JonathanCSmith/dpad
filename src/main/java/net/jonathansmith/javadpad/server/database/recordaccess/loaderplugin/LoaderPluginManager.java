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
package net.jonathansmith.javadpad.server.database.recordaccess.loaderplugin;

import javax.persistence.NonUniqueResultException;

import org.hibernate.HibernateException;

import net.jonathansmith.javadpad.api.database.records.LoaderPluginRecord;
import net.jonathansmith.javadpad.server.database.connection.DatabaseConnection;
import net.jonathansmith.javadpad.server.database.recordaccess.GenericManager;

/**
 *
 * @author Jon
 */
public class LoaderPluginManager extends GenericManager<LoaderPluginRecord> {
    
    private static LoaderPluginManager instance = null;
    
    private LoaderPluginManager() {
        super(new LoaderPluginDAO(), LoaderPluginRecord.class);
    }
    
    public static LoaderPluginManager getInstance() {
        if (instance == null) {
            instance = new LoaderPluginManager();
        }
        
        return instance;
    }
    
    public LoaderPluginRecord findPluginByName(DatabaseConnection connection, String name) {
        LoaderPluginRecord plugin = null;
        try {
            connection.beginTransaction();
            plugin = this.getDAO().findByName(connection.getSession(), name);
            connection.commitTransaction();
            
        } catch (NonUniqueResultException ex) {
            this.engine.error("Query resulted in a non unique answer", ex);
        } catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }
        
        return plugin;
    }
    
    @Override
    public LoaderPluginDAO getDAO() {
        return (LoaderPluginDAO) this.dao;
    }
}
