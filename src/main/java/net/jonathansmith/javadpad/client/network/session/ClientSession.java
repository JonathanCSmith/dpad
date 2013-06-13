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
package net.jonathansmith.javadpad.client.network.session;

import java.util.EnumMap;
import java.util.Map;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.RequestType;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.database.DataRequestPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public final class ClientSession extends Session {
    
    private final Map<RequestType, Long> sessionDataTimestamp = new EnumMap<RequestType, Long> (RequestType.class);
    
    private String lockKey;
    
    public ClientSession(Engine eng, Channel c) {
        super(eng, c);
        this.incoming = new IncomingClientNetworkThread(eng, this, this.getSessionID());
        this.outgoing = new OutgoingClientNetworkThread(eng, this, this.getSessionID());
        this.start();
    }    
    
    public void setKey(String key) {
        this.lockKey = key;
    }

    @Override
    public void addData(String key, RequestType dataType, RecordsList<Record> data) {
        if (key.contentEquals(this.lockKey)) {
            this.fireChange(new DataArriveEvent(dataType));
            this.sessionData.put(dataType, data);
            this.sessionDataTimestamp.put(dataType, System.currentTimeMillis());
        }
    }

    public RecordsList<Record> checkoutData(RequestType dataType) {
        if (this.sessionDataTimestamp.containsKey(dataType)) {
            long entryTime = this.sessionDataTimestamp.get(dataType);
            
            if (System.currentTimeMillis() - entryTime > 300000) {
                Packet p = new DataRequestPacket(this.engine, this, dataType);
                this.addPacketToSend(PacketPriority.HIGH, p);
                return null;
            }
            
            return this.sessionData.get(dataType);
        }
        
        Packet p = new DataRequestPacket(this.engine, this, dataType);
        this.addPacketToSend(PacketPriority.HIGH, p);
        return null;
    }

    @Override
    public void updateData(String key, RequestType dataType, RecordsTransform data) {
        if (key.contentEquals(this.lockKey)) {
            if (!this.sessionData.containsKey(dataType)) {
                Packet p = new DataRequestPacket(this.engine, this, dataType);
                this.addPacketToSend(PacketPriority.HIGH, p);
                return;
            }
            
            RecordsList<Record> result = data.transform(this.sessionData.get(dataType));
            this.sessionData.put(dataType, result);
            this.sessionDataTimestamp.put(dataType, System.currentTimeMillis());
        }
    }
    
    @Override
    public void shutdown(boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }
}
