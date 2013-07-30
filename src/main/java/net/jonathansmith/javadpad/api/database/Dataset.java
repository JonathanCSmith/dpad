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
public abstract class Dataset extends Record {
    
    private boolean clientSidePerformed = false;
    private boolean serverSideSubmitted = false;
    private boolean serverSidePerformed = false;
    private PluginRecord plugin;
    
    @Column(name = "ClientProcessed")
    public boolean getHasBeenClientProcessed() {
        return this.clientSidePerformed;
    }
    
    public void setHasBeenClientProcessed(boolean value) {
        this.clientSidePerformed = value;
    }
    
    @Column(name = "ServerSubmitted")
    public boolean getHasBeenSubmittedToServer() {
        return this.serverSideSubmitted;
    }
    
    public void setHasBeenSubmittedToServer(boolean value) {
        this.serverSideSubmitted = value;
    }
    
    @Column(name = "ServerProcessed")
    public boolean getHasBeenServerProcessed() {
        return this.serverSidePerformed;
    }
    
    public void setHasBeenServerProcessed(boolean value) {
        this.serverSidePerformed = value;
    }
    
    @Column(name = "Plugin")
    public PluginRecord getPluginInfo() {
        return this.plugin;
    }
    
    public void setPluginInfo(PluginRecord record) {
        this.plugin = record;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Dataset) {
            Dataset d = (Dataset) o;
            if (this.getHasBeenServerProcessed() == d.getHasBeenServerProcessed()
                && this.getPluginInfo().equals(d.getPluginInfo())) {
                return true;
            }
        }
        
        return false;
    }
}