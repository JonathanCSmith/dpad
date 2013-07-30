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
package net.jonathansmith.javadpad.common.network.packet.database;

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.api.database.Record;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.database.recordaccess.QueryType;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class UpdateDataPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private Record data;
    private boolean pullsFocus;
    private byte[] serializedData;
    
    public UpdateDataPacket() {
        super();
    }
    
    public UpdateDataPacket(Engine engine, Session session, Record record, boolean pullsFocus) {
        super(engine, session);
        this.data = record;
        this.pullsFocus = pullsFocus;
        
        if (this.data == null) {
            this.engine.forceShutdown("Cannot update an existing record to null", null);
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
                if (this.pullsFocus) {
                    return new byte[] {1};
                }
                
                else {
                    return new byte[] {0};
                }
                
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
                if (bytes[0] == 0) {
                    this.pullsFocus = false;
                }
                
                else {
                    this.pullsFocus = true;
                }
                return;
                
            case 1:
                this.data = (Record) SerializationUtils.deserialize(bytes);
                return;
        }
    }

    @Override
    public void handleClientSide() {}

    @Override
    public void handleServerSide() {
        if (this.data != null) {
            ((ServerSession) this.session).updateDatabaseRecord(this.getKey(), this.data, true);
        }
        
        if (this.pullsFocus) {
            RecordsList<Record> out = new RecordsList<Record> ();
            SessionData focus = SessionData.getSessionDataFromDatabaseRecordAndQuery(this.data.getType(), QueryType.SINGLE);
            out.add(new IntegerRecord(focus.ordinal()));
            ((ServerSession) this.session).setSessionData(this.getKey(), SessionData.FOCUS, out, true);
        }
    }

    @Override
    public String toString() {
        return "Update record packet containing records of type: " + this.data.getType().toString().toLowerCase();
    }
}