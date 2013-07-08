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
package net.jonathansmith.javadpad.common.database.records;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "LoaderPlugin", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class LoaderPluginRecord extends PluginRecord {

    private Equipment equipment;
    private Set<String> fileExtensions = new HashSet<String> ();
    
    @ManyToOne
    public Equipment getEquipment() {
        return this.equipment;
    }
    
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
    
    @Column(name = "Extensions")
    @ElementCollection
    public Set<String> getAllowedExtensions() {
        return this.fileExtensions;
    }
    
    public void setAllowedExtensions(Set<String> ext) {
        this.fileExtensions = ext;
    }

    @Override
    public void addToChildren(Record record) {}

    @Override
    @Transient
    public DatabaseRecord getType() {
        return DatabaseRecord.LOADER_PLUGIN;
    }
    
    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            if (o instanceof LoaderPluginRecord) {
                LoaderPluginRecord p = (LoaderPluginRecord) o;
                if (this.getEquipment().equals(p.getEquipment())) {
                    return true;
                }
            }
        }
        
        return false;
    }
}