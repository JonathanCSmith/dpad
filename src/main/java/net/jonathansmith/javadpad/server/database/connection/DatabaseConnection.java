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
package net.jonathansmith.javadpad.server.database.connection;

import org.hibernate.Session;

/**
 *
 * @author Jon
 */
public class DatabaseConnection {
    
    private final Session sess;
    
    public DatabaseConnection(Session sess) {
        this.sess = sess;
    }
    
    public Session getSession() {
        return this.sess;
    }
    
    public void beginTransaction() {
        this.sess.beginTransaction();
    }
    
    public void commitTransaction() {
        this.sess.getTransaction().commit();
    }
    
    public void rollbackTransaction() {
        this.sess.getTransaction().rollback();
    }
    
    public void closeConnection() {
        this.sess.close();
    }
}
