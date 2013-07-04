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
import net.jonathansmith.javadpad.server.network.session.ServerSession;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class SetSessionDataPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private SessionData type;
    private RecordsList<Record> data = null;
    private boolean pullsFocus;
    private byte[] serializedData;
    
    public SetSessionDataPacket() {
        super();
    }
    
    public SetSessionDataPacket(Engine engine, Session session, SessionData type, RecordsList<Record> data, boolean pullsFocus) {
        super(engine, session);
        this.type = type;
        this.data = data;
        this.pullsFocus = pullsFocus;
        
        if (data != null) {
            this.serializeData();
        }
    }
    
    private void serializeData() {
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
        return 3;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
            case 1:
                return 1;
                
            case 2:
                return this.data != null ? this.serializedData.length : 0;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                byte[] out = new byte[1];
                out[0] = (byte) this.type.ordinal();
                return out;
                
            case 1:
                if (this.pullsFocus) {
                    return new byte[] {1};
                }
                
                else {
                    return new byte[] {0};
                }
                
            case 2:
                return this.data != null ? this.serializedData : null;
                
            default:
                return null;
        }
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.type = SessionData.values()[bytes[0]];
                return;
                
            case 1:
                if (bytes[0] == 0) {
                    this.pullsFocus = false;
                }
                
                else {
                    this.pullsFocus = true;
                }
                
            case 2:
                this.data = (RecordsList<Record>) SerializationUtils.deserialize(bytes);
        }
    }

    @Override
    public void handleClientSide() {
        ((ClientSession) this.session).setSessionData(this.getKey(), this.type, this.data);
    }

    @Override
    public void handleServerSide() {
        ((ServerSession) this.session).setSessionData(this.getKey(), this.type, this.data);
        
        if (this.pullsFocus) {
            ((ServerSession) this.session).setSessionData(this.getKey(), SessionData.FOCUS, this.data);
        }
    }

    @Override
    public String toString() {
        return "Set session data packet for data type: " + this.type.toString().toLowerCase();
    }
}
