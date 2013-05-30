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
package net.jonathansmith.javadpad.database.batch;

import java.io.Serializable;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import net.jonathansmith.javadpad.database.dataset.DataSet;
import net.jonathansmith.javadpad.database.equipment.Equipment;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Batch", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class Batch implements Serializable {
    
    private String uuid;
    private Equipment equipment;
    private Set<DataSet> dataGroup;
    
    public Batch() {}
    
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
    
    @Column(name = "Equipment")
    public Equipment getEquipment() {
        return this.equipment;
    }
    
    public void setEquipment(Equipment eqp) {
        this.equipment = eqp;
    }
    
    @Column(name = "DataGroup")
    @OneToMany(orphanRemoval = true)
    public Set<DataSet> getDataGroup() {
        return this.dataGroup;
    }
    
    public void setDataGroup(Set<DataSet> dataGroup) {
        this.dataGroup = dataGroup;
    }
}
