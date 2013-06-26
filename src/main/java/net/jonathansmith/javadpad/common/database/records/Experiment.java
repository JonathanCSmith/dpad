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

import javax.persistence.Column;
import javax.persistence.Entity;
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
    
    private Set<RawDataSet> loadedData;
    private Set<ProcessedDataSet> processedData;
    private Set<AnalysedDataSet> analysedData;
    
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
    @OneToMany(orphanRemoval = true)
    public Set<RawDataSet> getLoadedData() {
        return this.loadedData;
    }
    
    public void setLoadedData(Set<RawDataSet> data) {
        this.loadedData = data;
    }
    
    public void addLoadedData(RawDataSet data) {
        if (!this.loadedData.contains(data)) {
            this.loadedData.add(data);
        }
    }
    
    public void removeLoadedData(RawDataSet data) {
        if (this.loadedData.contains(data)) {
            this.loadedData.remove(data);
        }
    }
    
    @Column(name = "ProcessedData")
    @OneToMany(orphanRemoval = true)
    public Set<ProcessedDataSet> getProcessedData() {
        return this.processedData;
    }
    
    public void setProcessedData(Set<ProcessedDataSet> data) {
        this.processedData = data;
    }
    
    public void addProcessedData(ProcessedDataSet data) {
        if (!this.processedData.contains(data)) {
            this.processedData.add(data);
        }
    }
    
    public void removeProcessedData(ProcessedDataSet data) {
        if (this.processedData.contains(data)) {
            this.processedData.remove(data);
        }
    }
    
    @Column(name = "AnalysedData")
    @OneToMany(orphanRemoval = true)
    public Set<AnalysedDataSet> getAnalysedData() {
        return this.analysedData;
    }
    
    public void setAnalysedData(Set<AnalysedDataSet> data) {
        this.analysedData = data;
    }
    
    public void addAnalysedData(AnalysedDataSet data) {
        if (!this.analysedData.contains(data)) {
            this.analysedData.add(data);
        }
    }
    
    public void removeAnalysedData(AnalysedDataSet data) {
        if (this.analysedData.contains(data)) {
            this.analysedData.remove(data);
        }
    }
   
    @Override
    public boolean equals(Object o) {
        if (o instanceof Experiment) {
            Experiment e = (Experiment) o;
            if (this.getLoadedData().equals(e.getLoadedData())
                && this.getProcessedData().equals(e.getProcessedData())
                    && this.getAnalysedData().equals(e.getAnalysedData())
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
