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
package net.jonathansmith.javadpad.database.user;

import java.io.Serializable;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.database.experiment.Experiment;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "User", uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class User implements Serializable {
    
    private String uuid;
    private String username;
    private String password;
    private Set<Experiment> experiments;
    
    public User() {}
    
    public User(String username, String password) {
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
    
    @Column(name = "Experiment")
    @OneToMany(orphanRemoval = true)
    public Set<Experiment> getExperiments() {
        return this.experiments;
    }
    
    public void setExperiments(Set<Experiment> experiments) {
        this.experiments = experiments;
    }
}
