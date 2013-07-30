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
package net.jonathansmith.javadpad.common.network.packet.plugins;

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.api.Platform;
import net.jonathansmith.javadpad.api.database.PluginRecord;
import net.jonathansmith.javadpad.api.database.records.LoaderDataset;
import net.jonathansmith.javadpad.api.plugin.IPlugin;
import net.jonathansmith.javadpad.api.threads.IRuntime;
import net.jonathansmith.javadpad.api.threads.IThread;
import net.jonathansmith.javadpad.api.utils.ILogger;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.server.Server;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class RunLoaderPluginPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private PluginRecord plugin;
    private LoaderDataset target;
    private byte[] serializedPlugin;
    private byte[] serializedTarget;
    
    public RunLoaderPluginPacket() {
        super();
    }
    
    public RunLoaderPluginPacket(Engine engine, Session session, PluginRecord plugin, LoaderDataset data) {
        super(engine, session);
        this.plugin = plugin;
        this.target = data;
        
        if (this.plugin == null || this.target == null) {
            this.engine.forceShutdown("Cannot run a plugin with null data", null);
        }
        
        this.serializedPlugin = SerializationUtils.serialize(this.plugin);
        this.serializedTarget = SerializationUtils.serialize(this.target);
    }
    
    @Override
    public int getID() {
        return id;
    }
    
    @Override
    public void setID(int newID) {
        if (lock.compareAndSet(false, true)) {
            id = newID;
        }
    }
    
    @Override
    public int getNumberOfLockedPayloads() {
        return 2;
    }
    
    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return this.serializedPlugin.length;
                
            case 1:
                return this.serializedTarget.length;
                
            default:
                return 0;
        }
    }
    
    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return this.serializedPlugin;
                
            case 1:
                return this.serializedTarget;
                
            default:
                return null;
        }
    }
    
    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.plugin = (PluginRecord) SerializationUtils.deserialize(bytes);
                break;
                
            case 1:
                this.target = (LoaderDataset) SerializationUtils.deserialize(bytes);
                break;
        }
    }
    
    @Override
    public void handleClientSide() {}
    
    @Override
    public void handleServerSide() {
        IPlugin plugin = this.engine.getPluginManager().getPlugin(this.plugin.getName());
        if (plugin != null) {
            IRuntime serverThread = plugin.getRuntimeThread(Platform.SERVER, (ILogger) this.engine);
            IThread thread = serverThread.getThread();
            thread.setPayload(this.target);
            ((Server) this.engine).addWorkerThread(this.getKey(), this.plugin, thread);
            
            this.target.setHasBeenSubmittedToServer(true);
            ((ServerSession) this.session).updateDatabaseRecord(this.getKey(), this.target);
        }
    }
    
    @Override
    public String toString() {
        return "Plugin run packet for: " + this.plugin.getName() + " for Loader Datasets";
    }
}