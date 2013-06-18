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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.bouncycastle.util.Arrays;

import net.jonathansmith.javadpad.common.database.DataSet;
import net.jonathansmith.javadpad.common.database.Record;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Raw Data Sets", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class RawDataSet extends Record implements DataSet {
    
    private int[] data;
    private int[] times;
    private DataType dataType;
    private Equipment equipment;
    
    public RawDataSet() {
        super();
    }
    
    @Id
    @Column(name = "UUID", updatable = false, unique = true, nullable = false)
    public String getUUID() {
        return this.uuid;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
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
    
    @JoinColumn(name = "Data Type")
    @ManyToOne
    public DataType getDataType() {
        return this.dataType;
    }
    
    public void setDataType(DataType data) {
        this.dataType = data;
    }
    
    @JoinColumn(name = "Equipment")
    @ManyToOne
    public Equipment getEquipment() {
        return this.equipment;
    }
    
    public void setEquipment(Equipment data) {
        this.equipment = data;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof RawDataSet) {
            RawDataSet d = (RawDataSet) o;
            
            if (this.getUUID().contentEquals(d.getUUID())
                && Arrays.areEqual(this.getData(), d.getData())
                    && Arrays.areEqual(this.getTimes(), d.getTimes())
                    && this.getDataType().equals(d.getDataType())
                    && this.getEquipment().equals(d.getEquipment())) {
                return true;
            }
        }
        
        return false;
    }
}
