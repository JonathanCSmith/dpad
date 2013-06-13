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
package net.jonathansmith.javadpad.common.util.database;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Jon
 */
public class RecordsList<T> extends LinkedList<T> {
    
    @Override
    public boolean contains(Object o) {
        Iterator iter = this.descendingIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj.hashCode() == o.hashCode()) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public int indexOf(Object o) {
        Iterator iter = this.descendingIterator();
        int i = 0;
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj.hashCode() == o.hashCode()) {
                return i;
            }
            
            i++;
        }
        
        return -1;
    }
    
    public boolean isOutdated(Object newObj, Object oldObj) {
        Iterator iter = this.descendingIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj.hashCode() == oldObj.hashCode()) {
                if (obj.equals(newObj)) {
                    return false;
                }
                
                return true;
            }
        }
        
        return false;
    }
}
