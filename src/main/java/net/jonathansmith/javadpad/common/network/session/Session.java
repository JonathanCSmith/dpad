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
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.DataSet;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.database.records.Experiment;
import net.jonathansmith.javadpad.common.database.records.User;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.ChangeSender;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyResponsePacket;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
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
    
    private final Map<SessionData, RecordsList<Record>> sessionData = new EnumMap<SessionData, RecordsList<Record>> (SessionData.class);
    
    private final Random random = new Random();
    private final String id = Long.toString(random.nextLong(), 16).trim();
    private final CopyOnWriteArrayList<ChangeListener> listeners;
    private final AtomicBoolean lock = new AtomicBoolean(false);
    
    public NetworkThread incoming;
    public NetworkThread outgoing;
    
    private NetworkThreadState state = NetworkThreadState.EXCHANGING_HANDSHAKE;
    private String serverKey;
    private User user;
    private Experiment experiment;
    private DataSet currentData;
    
    public Session(Engine eng, Channel channel) {
        this.engine = eng;
        this.channel = channel;
        this.listeners = new CopyOnWriteArrayList<ChangeListener> ();
    }

    // Core properties
    protected final String getSessionID() {
        return this.id;
    }
    
    // Authentication
    public abstract void handleHandshake(HandshakePacket p);
    
    public abstract void handleEncryptionKeyRequest(EncryptionKeyRequestPacket p);
    
    public abstract void handleEncryptionKeyResponse(EncryptionKeyResponsePacket p, boolean isReply);
    
    public abstract void handleSessionKey(EncryptedSessionKeyPacket p);
    
    public final NetworkThreadState getState() {
        return this.state;
    }
    
    protected final void incrementState() {
        if (this.state == NetworkThreadState.RUNNING) {
            return;
        }
        
        int currentState = this.state.ordinal();
        this.state = NetworkThreadState.values()[currentState + 1];
        
        if (this.state == NetworkThreadState.RUNNING) {
            this.engine.info("Session: " + this.getSessionID() + " has been authenticated for full interaction");
        }
    }
    
    // Network handlerssss
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
    protected final void setServerKey(String key) {
        if (this.lock.compareAndSet(false, true)) {
            this.serverKey = key;
        }
    }
    
    protected final boolean isServerKey(String key) {
        return key.contentEquals(this.serverKey);
    }
    
    public final void lockAndSendPacket(PacketPriority priority, LockedPacket packet) {
        packet.lockPacket(this.serverKey);
        this.addPacketToSend(priority, packet);
    }
    
    public abstract void addData(String key, SessionData dataType, RecordsList<Record> data);
    
    public abstract void updateData(String key, SessionData dataType, RecordsTransform data);
    
    public abstract RecordsList<Record> checkoutData(SessionData dataType);
    
    // Locked read writers of session data
    protected final boolean addSessionData(String key, SessionData dataType, RecordsList<Record> data) {
        if (dataType == null) {
            return false;
        }
        
        if (key.contentEquals(this.serverKey)) {
            this.sessionData.put(dataType, data);
            return true;
        }
        
        return false;
    }
    
    protected final RecordsList<Record> checkoutSessionData(SessionData dataType) {
        if (dataType == null) {
            return null;
        }
        
        return this.sessionData.get(dataType);
    }
    
    protected final void clearSessionData() {
        this.sessionData.clear();
    }
    
    // Core session properties - what the ui will interact with
    public abstract void setKeySessionData(String key, DatabaseRecord type, Record data);
    
    // TODO: Any better? consolidate switch block with database accessors????
    public Record getKeySessionData(DatabaseRecord type) {
        switch (type) {
            case USER:
                return this.getUser();
                
            case EXPERIMENT:
                return this.getExperiment();
                
            default:
                return null;
        }
    }
    
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
    
    public DataSet getCurrentData() {
        return this.currentData;
    }
    
    protected void setCurrentData(DataSet data) {
        this.currentData = data;
    }
    
    // Functional methods
    public final void start() {
        this.incoming.start();
        this.outgoing.start();
    }

    public abstract void disconnect(boolean force);
    
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
