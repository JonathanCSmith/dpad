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
package net.jonathansmith.javadpad.api.database.records;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.api.database.DatabaseRecord;
import net.jonathansmith.javadpad.api.database.Dataset;
import net.jonathansmith.javadpad.api.database.Record;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "LoaderDataset", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class LoaderDataset extends Dataset {
    
    private Set<String> fileNames = new HashSet<String> ();
    private Template template;
    private Set<TimeCourseData> dataValues = new HashSet<TimeCourseData> ();
    
    public LoaderDataset() {
        super();
    }
    
    @Column(name = "SourceFiles")
    @ElementCollection
    public Set<String> getSourceFiles() {
        return this.fileNames;
    }
    
    public void setSourceFiles(Set<String> files) {
        this.fileNames = files;
    }
    
    public void addSourceFile(String fileName) {
        if (!this.fileNames.contains(fileName)) {
            this.fileNames.add(fileName);
        }
    }
    
    @ManyToOne
    public Template getSampleOrder() {
        return this.template;
    }
    
    public void setSampleOrder(Template temp) {
        this.template = temp;
    }
    
    @Column(name = "TimeCourseData")
    @OneToMany(orphanRemoval = true)
    public Set<TimeCourseData> getData() {
        return this.dataValues;
    }
    
    public void setData(Set<TimeCourseData> data) {
        this.dataValues = data;
    }
    
    public void addData(TimeCourseData data) {
        this.dataValues.add(data);
    }

    @Override
    public void addToChildren(Record record) {
        if (record instanceof TimeCourseData) {
            this.addData((TimeCourseData) record);
        }
    }

    @Override
    @Transient
    public DatabaseRecord getType() {
        return DatabaseRecord.LOADER_DATA;
    }
    
    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            if (o instanceof LoaderDataset) {
                LoaderDataset l = (LoaderDataset) o;
                if (this.getSourceFiles().equals(l.getSourceFiles())
                    && this.getSampleOrder().equals(l.getSampleOrder())
                        && this.getData().equals(l.getData())) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
