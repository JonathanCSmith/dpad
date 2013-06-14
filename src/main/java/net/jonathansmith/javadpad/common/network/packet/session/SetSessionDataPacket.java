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

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class SetSessionDataPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private SessionData type;
    private Record data;
    private byte[] serializedData;
    
    public SetSessionDataPacket() {
        super();
    }
    
    public SetSessionDataPacket(Engine engine, Session session, SessionData type, Record record) {
        super(engine, session);
        this.type = type;
        this.data = record;
        
        this.serializeData();
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
    public int getNumberOfPayloads() {
        return 3;
    }

    @Override
    public int getPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return this.key.getBytes().length;
                
            case 1:
                return 1;
                
            case 2:
                return this.serializedData.length;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writePayload(int payloadNumber, int providedSize) {
        switch (payloadNumber) {
            case 0:
                return this.key.getBytes();
                
            case 1:
                byte[] out = new byte[1];
                out[0] = (byte) this.type.ordinal();
                return out;
                
            case 2:
                return this.serializedData;
                
            default:
                return null;
        }
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.key = new String(bytes);
                return;
                
            case 1:
                this.type = SessionData.values()[bytes[0]];
                return;
                
            case 2:
                this.data = (Record) SerializationUtils.deserialize(bytes);
        }
    }

    @Override
    public void handleClientSide() {
        this.session.setSessionData(this.key, this.type, this.data);
    }

    @Override
    public void handleServerSide() {
        this.session.setSessionData(this.key, this.type, this.data);
    }

    @Override
    public String toString() {
        return "Set session data packet for data type: " + this.type.toString().toLowerCase();
    }
}
