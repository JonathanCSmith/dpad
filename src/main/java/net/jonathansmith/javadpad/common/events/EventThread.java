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
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 *
 * @author Jon
 */
public class EventThread extends Thread {
    
    private Multimap<Class<? extends DPADEvent>, EventListener> eventMap = ArrayListMultimap.create();
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
                DPADEvent event = this.eventList.get(0);
                Class<? extends DPADEvent> eventClass = event.getClass();
                Collection<EventListener> listeners = this.eventMap.get(eventClass);
                for (EventListener listener : listeners) {
                    listener.changeEventReceived(this.eventList.get(0));
                }
                
                this.eventList.remove(0);
            }
        }
        
        this.eventList.clear();
        this.eventMap.clear();
    }
    
    public void shutdown(boolean force) {
        if (force) {
            this.eventList.clear();
        }
        
        this.isAlive = false;
    }
    
    public void addListener(Class<? extends DPADEvent> clazz, EventListener listener) {
        this.eventMap.put(clazz, listener);
    }
    
    public void removeListener(EventListener listener) {
        for (Entry entry : this.eventMap.entries()) {
            if (entry.getValue().equals(listener)) {
                this.eventMap.remove(entry.getKey(), listener);
            }
        }
    }
    
    public void removeListenerFromEvent(Class<? extends DPADEvent> clazz, EventListener listener) {
        if (this.eventMap.get(clazz).contains(listener)) {
            this.eventMap.remove(clazz, listener);
        }
    }
    
    public void post(DPADEvent event) {
        this.eventList.add(event);
    }
}