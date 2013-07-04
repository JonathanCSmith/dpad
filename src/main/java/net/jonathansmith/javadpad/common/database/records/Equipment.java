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
package net.jonathansmith.javadpad.common.database.records;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.Record;

/**
 *
 * @author jonathansmith
 */
@Entity
@Table(name = "Equipment", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class Equipment extends Record {
    
    private String equipmentUUID;
    private String name;
    private String description;
    
    public Equipment() {}
    
    @Column(name = "EquipmentUUID", updatable = false, unique = true, nullable = false)
    public String getEquipmentUUID() {
        return this.equipmentUUID;
    }
    
    public void setEquipmentUUID(String uuid) {
        this.equipmentUUID = uuid;
    }
    
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
    public DatabaseRecord getType() {
        return DatabaseRecord.EQUIPMENT;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Equipment) {
            Equipment e = (Equipment) o;
            if (this.getUUID().contentEquals(e.getUUID()) 
                && this.getName().contentEquals(e.getName()) 
                    && this.getDescription().contentEquals(e.getDescription()) 
                    && this.getEquipmentUUID().contentEquals(e.getEquipmentUUID())) {
                return true;
            }
            
            return false;
        }
        
        return false;
    }
}
