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
package net.jonathansmith.javadpad.database.datatype;

import javax.persistence.NonUniqueResultException;

import org.hibernate.HibernateException;

import net.jonathansmith.javadpad.database.DatabaseConnection;
import net.jonathansmith.javadpad.database.GenericManager;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public class DataTypeManager extends GenericManager<DataType> {
    
    private static DataTypeManager instance;
    
    private DataTypeManager() {
        super(new DataTypeDAO(), DataType.class);
    }
    
    public static DataTypeManager getInstance() {
        if (instance == null) {
            instance = new DataTypeManager();
        }
        
        return instance;
    }
    
    public DataType findDataTypeByName(String name) {
        DataType dataType = null;
        try {
            DatabaseConnection.beginTransaction();
            dataType = this.getDAO().findByName(name);
            DatabaseConnection.commitTransaction();
            
        } catch (NonUniqueResultException ex) {
            DPADLogger.severe("Query resulted in a non unique answer");
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return dataType;
    }
    
    @Override
    public DataTypeDAO getDAO() {
        return (DataTypeDAO) this.dao;
    }
}
