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
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class SetSessionDataPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private DatabaseRecord type;
    private Record data = null;
    private byte[] serializedData;
    
    public SetSessionDataPacket() {
        super();
    }
    
    public SetSessionDataPacket(Engine engine, Session session, DatabaseRecord type, Record record) {
        super(engine, session);
        this.type = type;
        this.data = record;
        
        if (record != null) {
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
        return 2;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return 1;
                
            case 1:
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
                return this.data != null ? this.serializedData : null;
                
            default:
                return null;
        }
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.type = DatabaseRecord.values()[bytes[0]];
                return;
                
            case 1:
                this.data = (Record) SerializationUtils.deserialize(bytes);
        }
    }

    @Override
    public void handleClientSide() {
        this.session.setKeySessionData(this.getKey(), this.type, this.data);
    }

    @Override
    public void handleServerSide() {
        this.session.setKeySessionData(this.getKey(), this.type, this.data);
    }

    @Override
    public String toString() {
        return "Set session data packet for data type: " + this.type.toString().toLowerCase();
    }
}
