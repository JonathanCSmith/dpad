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
package net.jonathansmith.javadpad.database.equipment;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import org.hibernate.HibernateException;

import net.jonathansmith.javadpad.database.DatabaseConnection;
import net.jonathansmith.javadpad.database.GenericManager;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public class EquipmentManager extends GenericManager<Equipment> {
    
    private static EquipmentManager instance = null;
    
    private EquipmentManager() {
        super(new EquipmentDAO(), Equipment.class);
    }
    
    public static EquipmentManager getInstance() {
        if (instance == null) {
            instance = new EquipmentManager();
        }
        
        return instance;
    }
    
    public Equipment findEquipmentByEquipmentID(String uuid) {
        Equipment equipment = null;
        try {
            DatabaseConnection.beginTransaction();
            equipment = this.getDAO().findByEquipmentUUID(uuid);
            DatabaseConnection.commitTransaction();
            
        } catch(NonUniqueResultException ex) {
            DPADLogger.severe("Query resulted in a non unique answer");
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return equipment;
    }
    
    @Override
    public EquipmentDAO getDAO() {
        return (EquipmentDAO) this.dao;
    }
}
