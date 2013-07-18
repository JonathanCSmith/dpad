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
package net.jonathansmith.javadpad.common.network.packet.session;

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class SessionDataPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);

    private static int id;
    
    private SessionData dataType;
    private RecordsList<Record> data;
    private byte[] serializedData;
    
    public SessionDataPacket() {
        super();
    }
    
    public SessionDataPacket(Engine engine, Session session, SessionData dataType, RecordsList<Record> data) {
        super(engine, session);
        this.dataType = dataType;
        this.data = data;
        this.serializeData();
    }
    
    private void serializeData() {
        if (this.data == null || this.data.size() == 0) {
            return;
        }
        
        this.serializedData = SerializationUtils.serialize(this.data);
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
        if (this.data == null || this.data.size() == 0) {
            return 1;
        }
        
        return 2;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return 1;
                
            case 1:
                return this.serializedData.length;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                byte[] out = new byte[1];
                out[0] = (byte) this.dataType.ordinal();
                return out;
                
            case 1:
                return this.serializedData;
                
            default:
                return null;
        }
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                int val = (int) bytes[0];
                this.dataType = SessionData.values()[val];
                break;
                
            case 1:
                RecordsList<Record> newData = (RecordsList<Record>) SerializationUtils.deserialize(bytes);
                this.data = newData;
        }
    }

    @Override
    public void handleClientSide() {
        if (this.data == null) {
            this.data = new RecordsList<Record> ();
        }
        
        ((ClientSession) this.session).setSessionData(this.getKey(), this.dataType, this.data);
    }

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        int size;
        if (this.data == null) {
            size = 0;
        }
        
        else {
            size = this.data.size();
        }
        
        return "Data packet containing: " + this.dataType.toString().toLowerCase() + " with " + size + " entries";
    }
}
