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
package net.jonathansmith.javadpad.api.threads;

import net.jonathansmith.javadpad.api.database.Dataset;
import net.jonathansmith.javadpad.api.database.PluginRecord;
import net.jonathansmith.javadpad.api.events.IEventSender;
import net.jonathansmith.javadpad.api.events.PluginFinishEvent;

/**
 *
 * @author Jon
 */
public abstract class IThread extends Thread {
    
    public IEventSender eventThread;
    public Dataset payload;
    
    private boolean isAlive = false;
    private boolean errored = false;
    
    public IThread() {}
    
    public final void init(IEventSender eventThread) {
        this.eventThread = eventThread;
        this.pluginInit();
    }
    
    public abstract void pluginInit();
    
    @Override
    public void start() {
        this.isAlive = true;
        this.pluginStart();
        super.start();
    }
    
    public abstract void pluginStart();
    
    @Override
    public void run() {
        while (this.isAlive) {
            this.pluginLoop();
        }
        
        this.finish(this.errored);
    }
    
    public abstract void pluginLoop();
    
    public abstract Dataset getPluginResult();
    
    public abstract PluginRecord getPlugin();
    
    public boolean isRunning() {
        return this.isAlive;
    }
    
    public void shutdown(boolean force) {
        this.errored = force;
        this.isAlive = false;
    }
    
    public void setPayload(Dataset data) {
        this.payload = data;
    }
    
    private void finish(boolean errored) {
        Dataset data = null;
        if (!errored) {
            data = this.getPluginResult();
        }
        
        PluginFinishEvent evt = new PluginFinishEvent(data, this.getPlugin());
        this.eventThread.post(evt);
    }
}