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
package net.jonathansmith.javadpad.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.service.ServiceRegistry;

import net.jonathansmith.javadpad.database.entry.DPADEntry;

/**
 *
 * @author Jon
 */
public class DatabaseConnection {
    
    public SessionFactory factory;
    public ServiceRegistry registry;
    
    public DatabaseConnection(SessionFactory factory, ServiceRegistry registry) {
        this.factory = factory;
        this.registry = registry;
    }
    
    public List listEntries(DPADEntry entry) {
        Transaction tx = null;
        Session sess = this.factory.getCurrentSession();
        
        try {
            tx = sess.beginTransaction();
            List entries = sess.createQuery("select h from " + entry.getTableName() + " as h").list();
            tx.commit();
            return entries;
            
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                    
                } catch (HibernateException ex2) {
                    // Exception handling here
                }
            }
            
            throw ex;
        }
    }
    
    public void createEntry(DPADEntry entry) {
        Transaction tx = null;
        Session sess = this.factory.getCurrentSession();
        
        try {
            tx = sess.beginTransaction();
            sess.save(entry);
            tx.commit();
            
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                    
                } catch (HibernateException ex2) {
                    // Exception handling here
                }
            }
            
            throw ex;
        }
    }
    
    public void deleteEntry(DPADEntry entry) {
        Transaction tx = null;
        Session sess = this.factory.getCurrentSession();
        
        try {
            tx = sess.beginTransaction();
            sess.delete(entry);
            tx.commit();
            
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                    
                } catch (HibernateException ex2) {
                    // Exception handling here
                }
            }
            
            throw ex;
        }
    }
    
    public void updateEntry(DPADEntry entry) {
        Transaction tx = null;
        Session sess = this.factory.getCurrentSession();
        
        try {
            tx = sess.beginTransaction();
            sess.update(entry);
            tx.commit();
            
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                    
                } catch (HibernateException ex2) {
                    // Exception handling here
                }
            }
            
            throw ex;
        }
    }
}
