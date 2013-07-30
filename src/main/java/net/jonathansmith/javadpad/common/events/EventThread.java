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

import net.jonathansmith.javadpad.api.events.Event;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.jonathansmith.javadpad.api.events.IEventSender;

/**
 *
 * @author Jon
 */
public class EventThread extends Thread implements IEventSender {
    
    private Multimap<Class<? extends Event>, EventListener> liveListenerMap = ArrayListMultimap.create();
    private Multimap<Class<? extends Event>, EventListener> snapshottedListenerMap = ArrayListMultimap.create();
    
    private List<Event> pendingEventList = new LinkedList<Event> ();
    private List<Event> liveEventList = new LinkedList<Event> ();
    
    private boolean isAlive = false;
    private boolean pendingListenerUpdates;
    private boolean pendingEventUpdates;
    private boolean isModifying;
    
    @Override
    public void start() {
        this.isAlive = true;
        super.start();
    }
    
    @Override
    public void run() {
        while (isAlive) {
            if (liveEventList.isEmpty()) {
                try {
                    Thread.sleep(100);
                }
                
                catch (InterruptedException ex) {
                    // TODO:
                }
            }
            
            else {
                this.isModifying = true;
                Event event = this.liveEventList.remove(0);
                this.isModifying = false;
                
                Class<? extends Event> eventClass = event.getClass();
                Collection<EventListener> listeners = this.snapshottedListenerMap.get(eventClass);
                for (EventListener listener : listeners) {
                    listener.changeEventReceived(event);
                }
            }
            
            if (pendingListenerUpdates) {
                this.snapshottedListenerMap.clear();
                this.snapshottedListenerMap.putAll(this.liveListenerMap);
                this.pendingListenerUpdates = false;
            }
            
            if (pendingEventUpdates) {
                if (!this.isModifying) {
                    this.liveEventList.addAll(this.pendingEventList);
                    this.pendingEventList.clear();
                    this.pendingEventUpdates = false;
                }
            }
        }
        
        this.pendingEventList.clear();
        this.liveListenerMap.clear();
        this.liveEventList.clear();
        this.snapshottedListenerMap.clear();
    }
    
    public void shutdown(boolean force) {
        if (force) {
            this.pendingListenerUpdates = false;
            this.liveEventList.clear();
        }
        
        this.isAlive = false;
    }
    
    public void addListener(Class<? extends Event> clazz, EventListener listener) {
        this.liveListenerMap.put(clazz, listener);
        this.pendingListenerUpdates = true;
    }
    
    public void removeListener(EventListener listener) {
        this.liveListenerMap.removeAll(listener);
        
        List<Class> entries = new LinkedList<Class> ();
        for (Entry entry : this.liveListenerMap.entries()) {
            if (entry.getValue().equals(listener)) {
                entries.add((Class) entry.getKey());
            }
        }
        
        for (Class entry : entries) {
            this.liveListenerMap.remove(entry, listener);
        }
        
        if (entries.size() > 0) {
            this.pendingListenerUpdates = true;
        }
    }
    
    public void removeListenerFromEvent(Class<? extends Event> clazz, EventListener listener) {
        if (this.liveListenerMap.get(clazz).contains(listener)) {
            this.liveListenerMap.remove(clazz, listener);
            this.pendingListenerUpdates = true;
        }
    }
    
    @Override
    public void post(Event event) {
        this.pendingEventList.add(event);
        this.pendingEventUpdates = true;
    }
}