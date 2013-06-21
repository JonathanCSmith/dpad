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
package net.jonathansmith.javadpad.server.database.recordsaccess;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.jolbox.bonecp.BoneCP;

import net.jonathansmith.javadpad.server.database.connection.DatabaseConnection;

/**
 *
 * @author Jon
 */
public class DatabaseManager {
    
    public SessionFactory factory;
    
    private BoneCP connectionPool = null;
    
    public DatabaseManager(SessionFactory factory) {
        this.factory = factory;
    }
    
    private SessionFactory getSessionFactory() {
        return this.factory;
    }
    
    public DatabaseConnection getConnection() {
        Session sess = this.getSessionFactory().openSession();
        DatabaseConnection connection = new DatabaseConnection(sess);
        return connection;
    }
    
    public void closeAll() {
        this.factory.close();
    }
}
