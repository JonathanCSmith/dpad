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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Experiment", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class Experiment extends Record {
    
    private String name;
    private String description;
    private Set<Batch> batches;
    
    public Experiment() {}
    
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
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Column(name = "BatchIDs")
    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER)
    public Set<Batch> getBatches() {
        return this.batches;
    }
    
    public void setBatches(Set<Batch> batches) {
        this.batches = batches;
    }
    
    public void addBatch(Batch id) {
        if (this.batches.contains(id)) {
            return;
        }
        
        this.batches.add(id);
    }
    
    public void removeBatch(Batch id) {
        if (this.batches.contains(id)) {
            this.batches.remove(id);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Experiment) {
            Experiment e = (Experiment) o;
            if (this.getBatches().equals(e.getBatches())
                && this.getDescription().contentEquals(e.getDescription())
                    && this.getName().contentEquals(e.getName())
                    && this.getUUID().contentEquals(e.getUUID())) {
                return true;
            }
            
            return false;
        }
        
        return false;
    }
}
