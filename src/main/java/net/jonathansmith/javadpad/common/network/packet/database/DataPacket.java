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

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.network.RequestType;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class DataPacket extends Packet {

    private static int id;
    
    private final AtomicBoolean lock = new AtomicBoolean(false);
    
    private String key;
    private RequestType dataType;
    private RecordsList<Record> data;
    private byte[] serializedData;
    
    public DataPacket() {
        super();
    }
    
    public DataPacket(Engine engine, Session session, String key, RequestType dataType, RecordsList<Record> data) {
        super(engine, session);
        this.key = key;
        this.dataType = dataType;
        this.data = data;
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
                out[0] = (byte) this.dataType.ordinal();
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
                break;
                
            case 1:
                this.dataType = RequestType.values()[bytes[0]];
                break;
                
            case 2:
                RecordsList<Record> newData = (RecordsList<Record>) SerializationUtils.deserialize(bytes);
                this.data = newData;
        }
    }

    @Override
    public void handleClientSide() {
        if (this.data == null) {
            this.data = new RecordsList<Record> ();
        }
        
        this.session.addData(this.key, this.dataType, this.data);
    }

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        return "Data packet containing: " + this.dataType.toString().toLowerCase() + " with " + this.data.size() + " entries";
    }
}
