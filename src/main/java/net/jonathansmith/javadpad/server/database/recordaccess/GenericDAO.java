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
package net.jonathansmith.javadpad.server.database.recordaccess;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class GenericDAO<T extends Record, ID extends Serializable> {
    
    public void save(Session sess, T entity) {
        sess.saveOrUpdate(entity);
    }
    
    public void merge(Session sess, T entity) {
        sess.merge(entity);
    }
    
    public void delete(Session sess, T entity) {
        sess.delete(entity);
    }
    
    public List<T> findMany(Query query) {
        List<T> t = (List<T>) query.list();
        return t;
    }
    
    public T findOne(Query query) {
        T t = (T) query.uniqueResult();
        return t;
    }
    
    public T findByID(Session sess, Class clazz, String uuid) {
        T t = (T) sess.get(clazz, uuid);
        return t;
    }
    
    public RecordsList<T> findAll(Session sess, Class clazz) {
        Query query = sess.createQuery("from " + clazz.getName());
        ArrayList<T> array = (ArrayList<T>) query.list();
        RecordsList<T> T = new RecordsList<T> ();
        
        for (int i = 0; i < array.size(); i++) {
            T.add(array.get(i));
        }
        
        return T;
    }
}
