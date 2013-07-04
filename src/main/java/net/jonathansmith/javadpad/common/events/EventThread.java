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
package net.jonathansmith.javadpad.common.events;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 *
 * @author Jon
 */
public class EventThread extends Thread {
    
    private Multimap<DPADEvent, ChangeListener> eventMap = ArrayListMultimap.create();
    private List<DPADEvent> eventList = new LinkedList<DPADEvent> ();
    private boolean isAlive = false;
    
    @Override
    public void start() {
        this.isAlive = true;
        super.start();
    }
    
    @Override
    public void run() {
        while (isAlive) {
            if (eventList.isEmpty()) {
                try {
                    Thread.sleep(100);
                }
                
                catch (InterruptedException ex) {
                    // TODO:
                }
            }
            
            else {
                Collection<ChangeListener> listeners = this.eventMap.get(this.eventList.get(0));
                for (ChangeListener listener : listeners) {
                    listener.changeEventReceived(this.eventList.get(0));
                }
                
                this.eventList.remove(0);
            }
        }
    }
    
    public void shutdown(boolean force) {
        if (force) {
            this.eventList.clear();
        }
        
        this.isAlive = false;
    }
    
    public void addListener(DPADEvent event, ChangeListener listener) {
        this.eventMap.put(event, listener);
    }
    
    public void sendEvent(DPADEvent event) {
        this.eventList.add(event);
    }
}