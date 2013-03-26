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
package net.jonathansmith.javadpad.database.user;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.NonUniqueResultException;

import org.hibernate.HibernateException;
import net.jonathansmith.javadpad.database.DatabaseConnection;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author Jon
 */
public class UserManager {
    
    private static UserManager instance = null;
    private UserDAO userDAO = new UserDAO();
    
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        
        return instance;
    }
    
    public User findUserByUsername(String name) {
        User user = null;
        try {
            DatabaseConnection.beginTransaction();
            user = userDAO.findByName(name);
            DatabaseConnection.commitTransaction();
            
        } catch (NonUniqueResultException ex) {
            DPADLogger.severe("Query resulted in a non unique answer");
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return user;
    }
    
    public List<User> loadUsers() {
        List<User> users = new ArrayList<User> ();
        try {
            DatabaseConnection.beginTransaction();
            users = userDAO.findAll(User.class);
            DatabaseConnection.commitTransaction();
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return users;
    }
    
    public boolean saveNewUser(User user) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            userDAO.save(user);
            DatabaseConnection.commitTransaction();
            success = true;
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return success;
    }
    
    public User findUserByID(String uuid) {
        User user = null;
        try {
            DatabaseConnection.beginTransaction();
            user = (User) userDAO.findByID(User.class, uuid);
            DatabaseConnection.commitTransaction();
            
        } catch (HibernateException ex) {
            DPADLogger.severe("Database query error");
            DPADLogger.logStackTrace(ex);
        }
        
        return user;
    }
    
    public boolean deleteUser(User user) {
        boolean success = false;
        try {
            DatabaseConnection.beginTransaction();
            userDAO.delete(user);
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
