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
package net.jonathansmith.javadpad.common.network.session;

import java.util.EnumMap;
import java.util.EventObject;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordPayloadType;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.database.records.Batch;
import net.jonathansmith.javadpad.common.database.records.Experiment;
import net.jonathansmith.javadpad.common.database.records.User;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.ChangeSender;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public abstract class Session implements ChangeSender {
    
    public enum NetworkThreadState {
        EXCHANGING_HANDSHAKE,
        EXCHANGING_AUTHENTICATION,
        RUNNING;
    }
    
    public final Channel channel;
    public final Engine engine;
    
    protected final Map<RecordPayloadType, RecordsList<Record>> sessionData = new EnumMap<RecordPayloadType, RecordsList<Record>> (RecordPayloadType.class);
    
    private final Random random = new Random();
    private final String id = Long.toString(random.nextLong(), 16).trim();
    private final CopyOnWriteArrayList<ChangeListener> listeners;
    
    public NetworkThread incoming;
    public NetworkThread outgoing;
    
    private NetworkThreadState state = NetworkThreadState.EXCHANGING_HANDSHAKE;
    private User user;
    private Experiment experiment;
    private Batch batch;
    
    public Session(Engine eng, Channel channel) {
        this.engine = eng;
        this.channel = channel;
        this.listeners = new CopyOnWriteArrayList<ChangeListener> ();
    }

    // Core properties
    protected final String getSessionID() {
        return this.id;
    }
    
    // Network handlers
    public NetworkThreadState getState() {
        return this.state;
    }
    
    public void incrementState() {
        if (this.state == NetworkThreadState.RUNNING) {
            return;
        }
        
        int currentState = this.state.ordinal();
        this.state = NetworkThreadState.values()[currentState + 1];
        
        if (this.state == NetworkThreadState.RUNNING) {
            this.engine.info("Session: " + this.getSessionID() + " has been authenticated for full interaction");
        }
    }
    
    public void addPacketToSend(PacketPriority priority, Packet p) {
        this.outgoing.addPacket(priority, p);
    }

    public void addPacketToReceive(PacketPriority priority, Packet p) {
        this.incoming.addPacket(priority, p);
    }
    
    public void sendPacketMessage(String key, PacketMessage pm) {
        if (!key.contentEquals(this.getSessionID())) {
            return;
        }
        
        this.channel.write(pm);
    }
    
    // Session Data
    public abstract void addData(String key, RecordPayloadType dataType, RecordsList<Record> data);
    
    public abstract void updateData(String key, RecordPayloadType dataType, RecordsTransform data);
    
    // Core session properties - what the ui will interact with
    public abstract void setSessionData(String key, SessionData type, Record data);
    
    
    public User getUser() {
        return this.user;
    }
    
    protected void setUser(User user) {
        this.user = user;
    }
    
    public Experiment getExperiment() {
        return this.experiment;
    }
    
    protected void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }
    
    public Batch getBatch() {
        return this.batch;
    }
    
    protected void setBatch(Batch batch) {
        this.batch = batch;
    }
    
    // Functional methods
    public final void start() {
        this.incoming.start();
        this.outgoing.start();
    }
    
    public abstract void shutdown(boolean force);

    public abstract void disconnect();
    
    public abstract void dispose();
    
    // Change senders
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
}
