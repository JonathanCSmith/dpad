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

import java.util.Arrays;
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
@Table(name = "Template", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class Template extends Record {
    
    private Set<Sample> samples;
    private boolean areTimesTemplated;
    private int[] times;
    
    public Template() {}
    
    @Column(name = "Samples")
    @OneToMany(orphanRemoval = true)
    public Set<Sample> getSamples() {
        return this.samples;
    }
    
    public void setSamples(Set<Sample> sam) {
        this.samples = sam;
    }
    
    public void addSample(Sample sam) {
        this.samples.add(sam);
    }
    
    @Column(name = "HasTimes")
    public boolean getHasTimes() {
        return this.areTimesTemplated;
    }
    
    public void setHasTimes(boolean val) {
        this.areTimesTemplated = val;
    }
    
    @Column(name = "Times")
    public int[] getTimes() {
        return this.times;
    }
    
    public void setTimes(int[] times) {
        this.times = times;
    }
    
    public void addTime(int time) {
        this.times[this.times.length] = time;
    }
    
    public void addSampleWithTime(Sample sample, int time) {
        this.addSample(sample);
        this.addTime(time);
    }
    
    @Override
    public boolean equals (Object o) {
        if (o instanceof Template) {
            Template t = (Template) o;
            if (this.getSamples().equals(t.getSamples())
                && this.getHasTimes() == t.getHasTimes()) {
                if (this.getHasTimes()) {
                    if (Arrays.equals(this.getTimes(), t.getTimes())) {
                        return true;
                    }
                }
                
                else {
                    return true;
                }
            }
        }
        
        return false;
    }
}
