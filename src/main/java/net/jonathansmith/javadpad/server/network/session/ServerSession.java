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
package net.jonathansmith.javadpad.server.network.session;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordPayloadType;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.database.records.User;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.database.DataPacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataUpdatePacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.database.user.UserManager;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 *
 * @author Jon
 */
public final class ServerSession extends Session {
    
    private final AtomicBoolean shaLock = new AtomicBoolean(false);
    
    private byte[] token;
    private String hash;
    
    public ServerSession(Engine eng, Channel channel) {
        super(eng, channel);
        this.incoming = new IncomingServerNetworkThread(eng, this, this.getSessionID());
        this.outgoing = new OutgoingServerNetworkThread(eng, this, this.getSessionID());
        this.start();
    }
    
    public byte[] getVerifyToken() {
        return this.token;
    }
    
    public void setVerifyToken(byte[] token) {
        this.token = token;
    }

    public void setSha1Hash(String sha1Hash) {
        this.hash = sha1Hash;
    }
    
    public String getSha1Hash() {
        return this.hash;
    }
    
    public void lockAndSendPacket(PacketPriority priority, LockedPacket packet) {
        packet.lockPacket(this.getSessionID());
        this.addPacketToSend(priority, packet);
    }

    // Session data
    @Override
    public void addData(String key, RecordPayloadType dataType, RecordsList<Record> data) {
        if (key.contentEquals(this.getSessionID())) {
            this.fireChange(new DataArriveEvent(dataType));
            this.sessionData.put(dataType, data);
        }
    }

    public void checkoutData(RecordPayloadType dataType) {
        RecordsList<Record> data = this.requestData(dataType);
        
        if (this.sessionData.containsKey(dataType)) {
            RecordsTransform transform = RecordsTransform.getTransform(this.sessionData.get(dataType), data);
            this.sessionData.put(dataType, transform.getData());
            
            LockedPacket p = new DataUpdatePacket(this.engine, this, dataType, transform);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p);
        }
        
        else {
            LockedPacket p = new DataPacket(this.engine, this, dataType, data);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p);
        }
    }

    @Override
    public void updateData(String key, RecordPayloadType dataType, RecordsTransform data) {
        if (key.contentEquals(this.getSessionID())) {
            RecordsList<Record> dataUpdate = this.requestData(dataType);
            this.sessionData.put(dataType, dataUpdate);
        }
    }
    
    @Override
    public void setSessionData(String key, SessionData type, Record data) {
        if (!key.contentEquals(this.getSessionID())) {
            return;
        }
        
        switch (type) {
            case USER:
                if (!(data instanceof User)) {
                    return;
                }
                
                this.setUser((User) data);
        }
    }
    
    @Override
    public void setUser(User user) {
        super.setUser(user);
        LockedPacket p = new SetSessionDataPacket(this.engine, this, SessionData.USER, (Record) user);
            this.lockAndSendPacket(PacketPriority.HIGH, p);
    }

    // Database
    // TODO: fix this with connection management
    public RecordsList<Record> requestData(RecordPayloadType dataType) {
        switch (dataType) {
            case ALL_USERS:
                return UserManager.getInstance().loadAll();
            default:
                return null;
        }
    }
    
    public void submitNewRecord(RecordPayloadType type, Record record) {
        switch (type) {
            case ALL_USERS:
                return;
                
            case USER:
                if (!(record instanceof User)) {
                    return;
                }
                
                UserManager.getInstance().saveNew((User) record);
                this.setSessionData(this.getSessionID(), SessionData.USER, record);
        }
    }
    
    // Runtime
    @Override
    public void shutdown(boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    @Override
    public void disconnect() {
        // TODO: Save session information
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }
    
    public void sha1Hash(Object[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(this.getSessionID().getBytes("ISO_8859_1"));
            
            for (Object o : input) {
                if (o instanceof String) {
                    md.update(((String) o).getBytes("ISO_8859_1"));
                }
                
                else if (o instanceof byte[]) {
                    md.update((byte[]) o);
                }
                
                else {
                    this.setHash(null);
                }
            }
            
            BigInteger bigInt = new BigInteger(md.digest());
            
            if (bigInt.compareTo(BigInteger.ZERO) < 0) {
                bigInt = bigInt.negate();
                this.setHash("-" + bigInt.toString(16));
            }
            
            else {
                this.setHash(bigInt.toString(16));
            }
        }
        
        catch (Exception ex) {
            this.engine.error("Error in sha1 digest", ex);
            this.setHash(null);
        }
    }
    
    private void setHash(String hash) {
        if (this.shaLock.compareAndSet(false, true)) {
            this.hash = hash;
        }
    }
}
