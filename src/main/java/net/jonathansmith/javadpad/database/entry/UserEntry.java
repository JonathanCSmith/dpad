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
package net.jonathansmith.javadpad.database.entry;

import java.io.Serializable;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "Users", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class UserEntry implements Serializable, DPADEntry {
    
    private String uuid;
    private String username;
    private String password;
    private Set<ExperimentEntry> experiments;
    
    public UserEntry() {}
    
    public UserEntry(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
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
    
    @Column(name = "Name")
    public String getName() {
        return this.username;
    }
    
    public void setName(String username) {
        this.username = username;
    }
    
    @Column(name = "Password")
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Column(name = "Experiments")
    @OneToMany(orphanRemoval = true)
    public Set<ExperimentEntry> getExperiments() {
        return this.experiments;
    }
    
    public void setExperiments(Set<ExperimentEntry> experiments) {
        this.experiments = experiments;
    }
    
    public String getTableName() {
        return "Users";
    }
}
