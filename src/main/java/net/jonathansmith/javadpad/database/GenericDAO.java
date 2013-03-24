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

import java.io.Serializable;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Jon
 */
public abstract class GenericDAO<T, ID extends Serializable> {
    
    protected Session getSession() {
        return DatabaseConnection.getSession();
    }
    
    public void save(T entity) {
        Session sess = this.getSession();
        sess.saveOrUpdate(entity);
    }
    
    public void merge(T entity) {
        Session sess = this.getSession();
        sess.merge(entity);
    }
    
    public void delete(T entity) {
        Session sess = this.getSession();
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
    
    public T findByID(Class clazz, String uuid) {
        Session sess = this.getSession();
        T t = (T) sess.get(clazz, uuid);
        return t;
    }
    
    public List findAll(Class clazz) {
        Session sess = this.getSession();
        Query query = sess.createQuery("from " + clazz.getName());
        List T = query.list();
        return T;
    }
}
