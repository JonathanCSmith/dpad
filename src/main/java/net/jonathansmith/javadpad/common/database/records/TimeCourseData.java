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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.common.database.Record;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "TimeCourseData", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class TimeCourseData extends Record {
    
    private int[] values;
    private int[] times;
    private DataType type;
    
    public TimeCourseData() {}
    
    @Column(name = "Values")
    public int[] getValues() {
        return this.values;
    }
    
    public void setValues(int[] val) {
        this.values = val;
    }
    
    @Column(name = "Times")
    public int[] getTimes() {
        return this.times;
    }
    
    public void setTimes(int[] time) {
        this.times = time;
    }
    
    @Column(name = "DataType")
    @ManyToOne
    public DataType getDataType() {
        return this.type;
    }
    
    public void setDataType(DataType type) {
        this.type = type;
    }
    
    @Override
    public boolean equals (Object o) {
        if (o instanceof TimeCourseData) {
            TimeCourseData t = (TimeCourseData) o;
            if (Arrays.equals(this.getValues(), t.getValues())
                && Arrays.equals(this.getTimes(), t.getTimes())
                    && this.getDataType().equals(t.getDataType())) {
                return true;
            }
        }
        
        return false;
    }
}
