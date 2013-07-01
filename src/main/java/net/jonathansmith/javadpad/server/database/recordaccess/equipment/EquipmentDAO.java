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
package net.jonathansmith.javadpad.server.database.recordaccess.equipment;

import org.hibernate.Query;
import org.hibernate.Session;

import net.jonathansmith.javadpad.common.database.records.Equipment;
import net.jonathansmith.javadpad.server.database.recordaccess.GenericDAO;

/**
 *
 * @author jonathansmith
 */
public class EquipmentDAO extends GenericDAO<Equipment, String> {
    
    public Equipment findByEquipmentUUID(Session sess, String uuid) {
        String sql = "Select p FROM Equipment p WHERE p.EquipmentUUID :uuid";
        Query query = sess.createQuery(sql).setParameter("uuid", uuid);
        Equipment equipment = findOne(query);
        return equipment;
    }
}
