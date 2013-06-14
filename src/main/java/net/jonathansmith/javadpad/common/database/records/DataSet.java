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

import java.util.Arrays;

import net.jonathansmith.javadpad.common.database.Record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author jonathansmith
 */
@Entity
@Table(name = "DataSet", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class DataSet extends Record {
    
    private int[] rawTimes;
    private int[] rawData;
    private int[] times;
    private int[] data;
    private DataType dataType;
    
    public DataSet() {}
    
    @Id
    @Column(name = "uuid", updatable = false, unique = true, nullable = false)
    public String getUUID() {
        return this.uuid;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    @Column(name = "RawTimes")
    public int[] getRawTimes() {
        return this.rawTimes;
    }
    
    public void setRawTimes(int[] times) {
        this.rawTimes = times;
    }
    
    @Column(name = "RawData")
    public int[] getRawData() {
        return this.rawData;
    }
    
    public void setRawData(int[] data) {
        this.rawData = data;
    }
    
    @Column(name = "DataType")
    public DataType getDataType() {
        return this.dataType;
    }
    
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    
    @Column(name = "Data")
    public int[] getData() {
        return this.data;
    }
    
    public void setData(int[] data) {
        this.data = data;
    }
    
    @Column(name = "Times")
    public int[] getTimes() {
        return this.times;
    }
    
    public void setTimes(int[] times) {
        this.times = times;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DataSet) {
            DataSet d = (DataSet) o;
            if (Arrays.equals(this.getData(), d.getData())
                && this.getDataType().equals(d.getDataType())
                    && Arrays.equals(this.getRawData(), d.getRawData())
                    && Arrays.equals(this.getRawTimes(), d.getRawTimes())
                    && Arrays.equals(this.getTimes(), d.getRawTimes())
                    && this.getUUID().contentEquals(d.getUUID())) {
                return true;
            }
            
            return false;
        }
        
        return false;
    }
}
