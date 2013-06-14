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

import java.util.Set;

import net.jonathansmith.javadpad.common.database.Record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Batch", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class Batch extends Record {
    
    private String name;
    private String description;
    private Equipment equipment;
    private Set<DataSet> dataSets;
    
    public Batch() {}
    
    @Id
    @Column(name = "uuid", updatable = false, unique = true, nullable = false)
    public String getUUID() {
        return this.uuid;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
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
    
    public void setDescription(String desc) {
        this.description = desc;
    }
    
    @JoinColumn(name = "Equipment")
    @ManyToOne
    public Equipment getEquipment() {
        return this.equipment;
    }
    
    public void setEquipment(Equipment eqp) {
        this.equipment = eqp;
    }
    
    @Column(name = "DataSet")
    @OneToMany(orphanRemoval = true)
    public Set<DataSet> getDataSets() {
        return this.dataSets;
    }
    
    public void setDataSets(Set<DataSet> dataSet) {
        this.dataSets = dataSet;
    }
    
    public void addDataSet(DataSet id) {
        if (this.dataSets.contains(id)) {
            return;
        }
        
        this.dataSets.add(id);
    }
    
    public void removeDataSet(DataSet id) {
        if (this.dataSets.contains(id)) {
            this.dataSets.remove(id);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Batch) {
            Batch b = (Batch) o;
            
            if (this.getUUID().contentEquals(b.getUUID()) 
                && this.getName().contentEquals(b.getName()) 
                    && this.getDescription().contentEquals(b.getDescription()) 
                    && this.getDataSets().equals(b.getDataSets()) 
                    && this.getEquipment().equals(b.getEquipment())) {
                return true;
            }
            
            return false;
        }
        
        return false;
    }
}
