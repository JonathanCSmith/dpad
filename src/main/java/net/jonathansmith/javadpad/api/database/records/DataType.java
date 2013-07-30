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
package net.jonathansmith.javadpad.api.database.records;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.api.database.DatabaseRecord;
import net.jonathansmith.javadpad.api.database.Record;

/**
 *
 * @author jonathansmith
 */
@Entity
@Table(name = "DataType", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class DataType extends Record {
    
    private String name;
    private String description;
    
    public DataType() {}
    
    @Column(name = "Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name = "Description")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void addToChildren(Record record) {}

    @Override
    @Transient
    public DatabaseRecord getType() {
        return DatabaseRecord.DATA_TYPE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DataType) {
            DataType d = (DataType) o;
            if (this.getDescription().contentEquals(d.getDescription())
                && this.getName().contentEquals(d.getName())
                    && this.getUUID().contentEquals(d.getUUID())) {
                return true;
            }
            
            return false;
        }
        
        return false;
    }
}