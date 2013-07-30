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

import org.hibernate.Query;
import org.hibernate.Session;

import net.jonathansmith.javadpad.api.database.records.LoaderPluginRecord;
import net.jonathansmith.javadpad.server.database.recordaccess.GenericDAO;

/**
 *
 * @author Jon
 */
public class LoaderPluginDAO extends GenericDAO<LoaderPluginRecord, String> {

    public LoaderPluginRecord findByName(Session sess, String username) {
        String sql = "SELECT p FROM LoaderPluginRecord p WHERE p.Name :name";
        Query query = sess.createQuery(sql).setParameter("name", username);
        LoaderPluginRecord plugin = findOne(query);
        return plugin;
    }
}
