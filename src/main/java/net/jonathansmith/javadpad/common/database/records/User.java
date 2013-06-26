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
@Table(name = "User", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class User extends Record {
    
    private String username;
    private String firstName;
    private String lastName;
    private char[] password;
    private int experimentNumber;
    private Set<Experiment> experiments;
    
    public User() {}
    
    public User(String username, String firstName, String lastName, char[] password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
    
    @Column(name = "Username", unique = true)
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Column(name = "Firstname")
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    @Column(name = "Lastname")
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    @Column(name = "Password")
    public char[] getPassword() {
        return this.password;
    }
    
    public void setPassword(char[] password) {
        this.password = password;
    }
    
    @Column(name = "ExperimentNumber")
    public int getNumberOfExperiments() {
        return this.experimentNumber;
    }
    
    public void setNumberOfExperiments(int i) {
        this.experimentNumber = i;
    }
    
    @Column(name = "Experiments")
    @OneToMany(orphanRemoval = true)
    public Set<Experiment> getExperiments() {
        return this.experiments;
    }
    
    public void setExperiments(Set<Experiment> experiments) {
        this.experiments = experiments;
        this.setNumberOfExperiments(this.experiments.size());
    }
    
    public void addExperiment(Experiment experiment) {
        if (this.experiments.contains(experiment)) {
            return;
        }
        
        this.experiments.add(experiment);
        this.experimentNumber++;
    }
    
    public void removeExperiment(Experiment experiment) {
        if (this.experiments.contains(experiment)) {
            this.experiments.remove(experiment);
            this.experimentNumber--;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User u = (User) o;
            if (this.getExperiments().equals(u.getExperiments())
                && this.getFirstName().contentEquals(u.getFirstName())
                    && this.getLastName().contentEquals(u.getLastName())
                    && Arrays.equals(this.getPassword(), u.getPassword())
                    && this.getUUID().contentEquals(u.getUUID())
                    && this.getUsername().contentEquals(u.getUsername())) {
                return true;
            }
            
            return false;
        }
        
        return false;
    }
}
