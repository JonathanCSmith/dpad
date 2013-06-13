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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class RecordsTransform {
    
    private final LinkedHashMap<Integer, Record> changes;
    private final LinkedList<Integer> deletions;
    private final RecordsList<Record> additions;
    
    public RecordsList<Record> data;
    
    public RecordsTransform(LinkedHashMap<Integer, Record> changes, LinkedList<Integer> deletions, RecordsList<Record> additions) {
        this.changes = changes;
        this.deletions = deletions;
        this.additions = additions;
    }
    
    public RecordsList<Record> transform(RecordsList<Record> oldData) {
        for (Map.Entry<Integer, Record> pair : changes.entrySet()) {
            oldData.set(pair.getKey(), pair.getValue());
        }
        
        for (Integer loc : this.deletions) {
            oldData.remove((int) loc);
        }
        
        for (Record obj : this.additions) {
            oldData.add(obj);
        }
        this.data = oldData;
        return oldData;
    }
    
    public LinkedHashMap<Integer, Record> getChanges() {
        return this.changes;
    }
    
    public LinkedList<Integer> getDeletions() {
        return this.deletions;
    }
    
    public RecordsList<Record> getAdditions() {
        return this.additions;
    }
    
    public RecordsList<Record> getData() {
        return this.data;
    }
    
    // TODO: verify that this works!
    public static RecordsTransform getTransform(RecordsList<Record> oldData, RecordsList<Record> newData) {
        LinkedHashMap<Integer, Record> changes = new LinkedHashMap<Integer, Record> ();
        LinkedList<Integer> deletions = new LinkedList<Integer> ();
        RecordsList<Record> additions = new RecordsList<Record> ();
        
        for (int i = 0; i < newData.size(); i++) {
            Record data = newData.get(i);
            int loc = oldData.indexOf(data);
            
            if (loc != -1) {
                changes.put(loc, data);
            }
        }
        
        for (int i = 0; i < oldData.size(); i++) {
            if (!newData.contains(oldData.get(i))) {
                deletions.add(i);
            }
        }
        
        for (int i = 0; i < newData.size(); i++) {
            Record data = newData.get(i);
            
            if (!oldData.contains(data)) {
                additions.add(data);
            }
        }
        
        RecordsTransform transform = new RecordsTransform(changes, deletions, additions);
        transform.transform(oldData);
        return transform;
    }
}
