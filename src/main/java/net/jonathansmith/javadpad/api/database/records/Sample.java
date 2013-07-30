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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.api.database.DatabaseRecord;
import net.jonathansmith.javadpad.api.database.Record;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Sample", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class Sample extends Record {
    
    private String name;
    private Set<String> conditions = new HashSet<String> ();
    
    public Sample() {}
    
    @Column(name = "Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name = "Conditions")
    @ElementCollection
    public Set<String> getConditions() {
        return this.conditions;
    }
    
    public void setConditions(Set<String> condition) {
        this.conditions = condition;
    }
    
    public void addCondition(String condition) {
        this.conditions.add(condition);
    }

    @Override
    public void addToChildren(Record record) {}

    @Override
    @Transient
    public DatabaseRecord getType() {
        return DatabaseRecord.SAMPLE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Sample) {
            Sample s = (Sample) o;
            if (this.getName().contentEquals(s.getName())
                && this.getConditions().equals(s.getConditions())) {
                return true;
            }
        }
        
        return false;
    }
}
