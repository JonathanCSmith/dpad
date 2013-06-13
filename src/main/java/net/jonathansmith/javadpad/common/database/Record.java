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
package net.jonathansmith.javadpad.common.database;

import java.io.Serializable;

import java.util.UUID;

import net.jonathansmith.javadpad.common.util.database.IdGenerator;

/**
 *
 * @author Jon
 */
public abstract class Record implements Serializable {
    
    public String uuid = IdGenerator.createId();
    
    @Override
    public abstract boolean equals(Object o);

    @Override
    public int hashCode() {
        UUID uid = UUID.fromString(this.uuid);
        return uuid.hashCode();
    }
}
