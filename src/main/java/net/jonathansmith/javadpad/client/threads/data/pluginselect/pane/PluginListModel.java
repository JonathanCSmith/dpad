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
package net.jonathansmith.javadpad.client.threads.data.pluginselect.pane;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import net.jonathansmith.javadpad.api.database.PluginRecord;
import net.jonathansmith.javadpad.api.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class PluginListModel extends AbstractListModel {
    
    public List<PluginRecord> plugins = new ArrayList<PluginRecord> ();
    
    public int getSize() {
        return this.plugins.size();
    }
    
    public void addElement(PluginRecord plugin) {
        this.plugins.add(plugin);
        this.fireIntervalAdded(this, this.plugins.size() - 1, this.plugins.size() - 1);
    }
    
    public Object getElementAt(int i) {
        PluginRecord plugin = this.plugins.get(i);
        return plugin.getName();
    }
    
    public String getInformation(int i) {
        PluginRecord plugin = this.plugins.get(i);
        return plugin.getDescription() + "/n Version: " + plugin.getVersion() + "/n Author:" + plugin.getAuthor() + "/n Organisation: " + plugin.getOrganization();
    }
    
    public PluginRecord getData(int i) {
        return this.plugins.get(i);
    }
    
    public void setData(RecordsList<Record> list) {
        this.plugins.clear();
        
        for (Record data : list) {
            if (data instanceof PluginRecord) {
                this.addElement((PluginRecord) data);
            }
        }
    }
    
    public void clearData() {
        this.plugins.clear();
    }
}
