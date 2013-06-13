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

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.RequestType;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataPacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataUpdatePacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.database.user.UserManager;

/**
 *
 * @author Jon
 */
public final class ServerSession extends Session {
    
    private byte[] token;
    private String hash;
    
    public ServerSession(Engine eng, Channel channel) {
        super(eng, channel);
        this.incoming = new IncomingServerNetworkThread(eng, this);
        this.outgoing = new OutgoingServerNetworkThread(eng, this);
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
    
    public void buildAndSendEncryptedSessionKeyPacket() {
        Packet p = new EncryptedSessionKeyPacket(this.engine, this, this.getSessionID());
        this.addPacketToSend(PacketPriority.CRITICAL, p);
    }

    // Session data
    @Override
    public void addData(String key, RequestType dataType, RecordsList<Record> data) {
        if (key.contentEquals(this.getSessionID())) {
            this.fireChange(new DataArriveEvent(dataType));
            this.sessionData.put(dataType, data);
        }
    }

    public void checkoutData(RequestType dataType) {
        RecordsList<Record> data = this.requestData(dataType);
        
        if (this.sessionData.containsKey(dataType)) {
            RecordsTransform transform = RecordsTransform.getTransform(this.sessionData.get(dataType), data);
            this.sessionData.put(dataType, transform.getData());
            
            Packet p = new DataUpdatePacket(this.engine, this, this.getSessionID(), dataType, transform);
            this.addPacketToSend(PacketPriority.MEDIUM, p);
            return;
        }
        
        else {
            Packet p = new DataPacket(this.engine, this, this.getSessionID(), dataType, data);
            this.addPacketToSend(PacketPriority.MEDIUM, p);
        }
    }

    @Override
    public void updateData(String key, RequestType dataType, RecordsTransform data) {
        if (key.contentEquals(this.getSessionID())) {
            RecordsList<Record> dataUpdate = this.requestData(dataType);
            this.sessionData.put(dataType, dataUpdate);
        }
    }

    // Database
    // TODO: fix this with connection management
    public RecordsList<Record> requestData(RequestType dataType) {
        switch (dataType) {
            case ALL_USERS:
                return UserManager.getInstance().loadAll();
            default:
                return null;
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
}
