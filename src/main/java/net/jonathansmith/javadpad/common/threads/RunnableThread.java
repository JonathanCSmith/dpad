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
package net.jonathansmith.javadpad.common.threads;

import java.util.EventObject;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.ChangeSender;

/**
 *
 * @author Jon
 */
public abstract class RunnableThread extends Thread implements ChangeSender {
    
    private final CopyOnWriteArrayList<ChangeListener> listeners;
    
    public RunnableThread() {
        this.listeners = new CopyOnWriteArrayList<ChangeListener> ();
    }
    
    @Override
    public void addListener(ChangeListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    @Override
    public void removeListener(ChangeListener listener) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }
    
    @Override
    public void fireChange(EventObject event) {
        for (ChangeListener listener : this.listeners) {
            listener.changeEventReceived(event);
        }
    }
    
    public abstract void init();
    
    public abstract void shutdown(boolean force);
}
