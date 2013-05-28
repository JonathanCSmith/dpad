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

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Batch", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class Batch implements Serializable {
    
    private String uuid;
    private Equipment equipment;
    private Set<DataGroup> dataGroup;
    
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
    
    /*
     * Batch needs an associated equipment entry, which, rather than storing the
     * entirety of the object information, need sufficient descriptive information
     * to help accurately re-identify from installed plugin
     * 
     * It needs a data group structure, which identifies which data sets are
     * associated with this peice of equipment, this can be a many association
     * each data group contains information on what is being observed and 
     * what the times are as well as the associated data
     */
    
    @Column(name = "equipment")
    public Equipment getEquipment() {
        return this.equipment;
    }
    
    public void setEquipment(Equipment eqp) {
        this.equipment = eqp;
    }
    
    @Column(name = "data group")
    @OneToMany(orphanRemoval = true)
    public Set<DataGroup> getDataGroup() {
        return this.dataGroup;
    }
    
    public void setDataGroup(Set<DataGroup> dataGroup) {
        this.dataGroup = dataGroup;
    }
}
