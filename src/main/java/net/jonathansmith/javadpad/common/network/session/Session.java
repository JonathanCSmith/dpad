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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.events.DPADEvent;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyResponsePacket;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public abstract class Session {
    
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
    private final AtomicBoolean lock = new AtomicBoolean(false);
    
    public NetworkThread incoming;
    public NetworkThread outgoing;
    
    private NetworkThreadState state = NetworkThreadState.EXCHANGING_HANDSHAKE;
    private String serverKey;
    
    public Session(Engine eng, Channel channel) {
        this.engine = eng;
        this.channel = channel;
    }

    // Core properties
    public final String getSessionID() {
        return this.id;
    }
    
    // Authentication
    public abstract void handleHandshake(HandshakePacket p);
    
    public abstract void handleEncryptionKeyRequest(EncryptionKeyRequestPacket p);
    
    public abstract void handleEncryptionKeyResponse(EncryptionKeyResponsePacket p, boolean isReply);
    
    public abstract void handleSessionKey(EncryptedSessionKeyPacket p);
    
    // Session Data
    protected final void setServerKey(String key) {
        if (this.lock.compareAndSet(false, true)) {
            this.serverKey = key;
        }
    }
    
    protected final boolean isServerKey(String key) {
        return key.contentEquals(this.serverKey);
    }
    
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
    
    // Network handlers
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
    
    public final void lockAndSendPacket(PacketPriority priority, LockedPacket packet) {
        packet.lockPacket(this.serverKey);
        this.addPacketToSend(priority, packet);
    }
    
    // Functional methods
    public final void start() {
        this.incoming.start();
        this.outgoing.start();
    }

    public abstract void disconnect(boolean force);
    
    public void fireChange(DPADEvent event) {
        this.engine.getEventThread().post(event);
    }
    
    /**
     * Checks out the focus of the current session, is always soft so as to not
     * packet spam. Code should rigourously set focus
     * 
     * @return records list containing the current focus data, can be empty if no
     * current focus
     */
    public final RecordsList<Record> getSessionFocusData() {
        SessionData focusType = this.getSessionFocusType();
        return this.checkoutSessionData(this.getSessionID(), focusType);
    }
    
    /**
     * Returns the current focus of the session, but contains no data
     * 
     * @return session data of the type that is currently focussed
     */
    public final SessionData getSessionFocusType() {
        RecordsList<Record> focusList = this.checkoutSessionData(this.getSessionID(), SessionData.FOCUS);
        if (focusList == null || focusList.isEmpty()) {
            return null;
        }
        
        Record focus = focusList.getFirst();
        if (focus == null) {
            return null;
        }
        
        return SessionData.values()[((IntegerRecord) focus).getValue()];
    }
    
    /**
     * Sets the session data under the assumption that the new data is always
     * correct
     * 
     * @param sourceKey
     * @param data
     * @return whether the set was successful or not
     */
    protected boolean setSessionData(String sourceKey, SessionData dataType, RecordsList<Record> data) {
        if (sourceKey.contentEquals(this.serverKey)) {
            this.sessionData.put(dataType, data);
            return true;
        }
        
        return false;
    }
    
    /**
     * Updates the session data
     * 
     * @param sourceKey
     * @param data 
     */
    public abstract void updateSessionData(String sourceKey, SessionData dataType, RecordsTransform data);
    
    /**
     * Check out session data from the data map, checks the key against itself
     * (Server should never ask for client data and client should request
     * through the session)
     * 
     * @param key
     * @param dataType
     * @return A list of records that may be null if no session data was present
     */
    public RecordsList<Record> checkoutSessionData(String sourceKey, SessionData dataType) {
        if (sourceKey.contentEquals(this.getSessionID())) {
            if (this.sessionData.containsKey(dataType)) {
                return this.sessionData.get(dataType);
            }
        }
        
        return null;
    }
    
    /**
     * Softly checks out any session data from the data map, the key ensures the
     * correct side is querying this and no packets will be sent
     * 
     * @param dataType
     * @return 
     */
    public abstract RecordsList<Record> softlyCheckoutSessionData(SessionData dataType);
    
    /**
     * Clears the session data if they have ownership
     * 
     * @param key 
     */
    protected final void clearSessionData(String key) {
        if (key.contentEquals(this.getSessionID())) {
            this.sessionData.clear();
        }
    }
}