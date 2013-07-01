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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.common.database.Record;


/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Experiment", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class Experiment extends Record {
    
    private String name;
    private String description;
    
    private Set<LoaderDataset> loadedData = new HashSet<LoaderDataset> ();
    private Set<AnalyserDataset> analysedData = new HashSet<AnalyserDataset> ();
    
    public Experiment() {}
    
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
    
    @Column(name = "RawData")
    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER)
    public Set<LoaderDataset> getLoadedData() {
        return this.loadedData;
    }
    
    public void setLoadedData(Set<LoaderDataset> data) {
        this.loadedData = data;
    }
    
    public void addLoadedData(LoaderDataset data) {
        if (!this.loadedData.contains(data)) {
            this.loadedData.add(data);
        }
    }
    
    public void removeLoadedData(LoaderDataset data) {
        if (this.loadedData.contains(data)) {
            this.loadedData.remove(data);
        }
    }
    
    @Column(name = "AnalysedData")
    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER)
    public Set<AnalyserDataset> getAnalyserData() {
        return this.analysedData;
    }
    
    public void setAnalyserData(Set<AnalyserDataset> data) {
        this.analysedData = data;
    }
    
    public void addAnalyserData(AnalyserDataset data) {
        if (!this.analysedData.contains(data)) {
            this.analysedData.add(data);
        }
    }
    
    public void removeAnalyserData(AnalyserDataset data) {
        if (this.analysedData.contains(data)) {
            this.analysedData.remove(data);
        }
    }
   
    @Override
    public boolean equals(Object o) {
        if (o instanceof Experiment) {
            Experiment e = (Experiment) o;
            if (this.getLoadedData().equals(e.getLoadedData())
                && this.getAnalyserData().equals(e.getAnalyserData())
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
