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
package net.jonathansmith.javadpad.api.database;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Jon
 */
@MappedSuperclass
public abstract class PluginRecord extends Record {
    
    private String name;
    private String description;
    private String version;
    private String author;
    private String organization;
    
    @Column(name = "Name", unique = true)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name = "Description")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Column(name = "Version")
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    @Column(name = "Author")
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    @Column(name = "Organization")
    public String getOrganization() {
        return this.organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof PluginRecord) {
            PluginRecord p = (PluginRecord) o;
            if (this.getUUID().contentEquals(p.getUUID())
                && this.getName().contentEquals(p.getName())
                    && this.getDescription().contentEquals(p.getDescription())
                    && this.getVersion().contentEquals(p.getVersion()) // TODO: use custom version matching
                    && this.getAuthor().contentEquals(p.getAuthor())
                    && this.getOrganization().contentEquals(p.getOrganization())) {
                return true;
            }
        }
        
        return false;
    }
}