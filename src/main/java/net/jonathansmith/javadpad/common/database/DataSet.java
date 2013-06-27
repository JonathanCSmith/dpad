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
package net.jonathansmith.javadpad.common.database;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Jon
 */
@MappedSuperclass
public abstract class DataSet extends Record {
    
    private boolean serverSidePerformed;
    private PluginRecord plugin;
    
    @Column(name = "Processed")
    public boolean getHasBeenProcessed() {
        return this.serverSidePerformed;
    }
    
    public void setHasBeenProcessed(boolean value) {
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
        if (o instanceof DataSet) {
            DataSet d = (DataSet) o;
            if (this.getHasBeenProcessed() == d.getHasBeenProcessed()
                && this.getPluginInfo().equals(d.getPluginInfo())) {
                return true;
            }
        }
        
        return false;
    }
}
