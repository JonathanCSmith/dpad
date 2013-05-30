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
package net.jonathansmith.javadpad.database.dataset;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import net.jonathansmith.javadpad.database.datatype.DataType;

/**
 *
 * @author jonathansmith
 */
@Entity
@Table(name = "DataGroup", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class DataSet implements Serializable {
    
    private String uuid;
    private int[] times;
    private int[] data;
    private DataType dataType;
    
    public DataSet() {}
    
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "uuid", updatable = false, unique = true, nullable = false)
    public String getUUID() {
        return this.uuid;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    @Column(name = "Times")
    public int[] getTimes() {
        return this.times;
    }
    
    public void setTimes(int[] times) {
        this.times = times;
    }
    
    @Column(name = "Data")
    public int[] getData() {
        return this.data;
    }
    
    public void setData(int[] data) {
        this.data = data;
    }
    
    @Column(name = "DataType")
    public DataType getDataType() {
        return this.dataType;
    }
    
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
