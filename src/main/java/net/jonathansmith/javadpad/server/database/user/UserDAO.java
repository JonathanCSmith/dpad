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
package net.jonathansmith.javadpad.server.database.user;

import net.jonathansmith.javadpad.common.database.User;
import org.hibernate.Query;

import net.jonathansmith.javadpad.server.database.DatabaseConnection;
import net.jonathansmith.javadpad.server.database.GenericDAO;

/**
 *
 * @author Jon
 */
public class UserDAO extends GenericDAO<User, String> {
    
    public User findByName(String username) {
        String sql = "SELECT p FROM User p WHERE p.name :name";
        Query query = DatabaseConnection.getSession().createQuery(sql).setParameter("name", username);
        User user = findOne(query);
        return user;
    }
}
