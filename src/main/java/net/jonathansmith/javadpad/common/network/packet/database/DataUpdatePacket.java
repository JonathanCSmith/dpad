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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.network.RequestType;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class DataUpdatePacket extends Packet {
    
    private static int id;
    
    private final AtomicBoolean lock = new AtomicBoolean(false);
    
    private String key;
    private RequestType dataType;
    private LinkedHashMap<Integer, Record> changes;
    private LinkedList<Integer> deletions;
    private RecordsList<Record> additions;
    
    private byte[] serializedChanges;
    private byte[] serializedDeletions;
    private byte[] serializedAdditions;
    
    public DataUpdatePacket() {
        super();
    }
    
    public DataUpdatePacket(Engine engine, Session session, String key, RequestType dataType, RecordsTransform transform) {
        super(engine, session);
        this.key = key;
        this.dataType = dataType;
        this.changes = transform.getChanges();
        this.deletions = transform.getDeletions();
        this.additions = transform.getAdditions();
        
        this.serializeData();
    }
    
    private void serializeData() {
        this.serializedChanges = SerializationUtils.serialize(this.changes);
        this.serializedDeletions = SerializationUtils.serialize(this.deletions);
        this.serializedAdditions = SerializationUtils.serialize(this.additions);
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
        return 5;
    }

    @Override
    public int getPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return this.key.getBytes().length;
                
            case 1:
                return 1;
                
            case 2:
                return this.serializedChanges.length;
                
            case 3:
                return this.serializedDeletions.length;
                
            case 4:
                return this.serializedAdditions.length;
                
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
                return this.serializedChanges;
                
            case 3:
                return this.serializedDeletions;
                
            case 4:
                return this.serializedAdditions;
                
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
                this.changes = (LinkedHashMap<Integer, Record>) SerializationUtils.deserialize(bytes);
                break;
                
            case 3:
                this.deletions = (LinkedList<Integer>) SerializationUtils.deserialize(bytes);
                break;
                
            case 4:
                this.additions = (RecordsList<Record>) SerializationUtils.deserialize(bytes);
                break;
        }
    }

    @Override
    public void handleClientSide() {
        if (this.changes == null) {
            this.changes = new LinkedHashMap<Integer, Record> ();
        }
        
        if (this.deletions == null) {
            this.deletions = new LinkedList<Integer> ();
        }
        
        if (this.additions == null) {
            this.additions = new RecordsList<Record> ();
        }
        
        RecordsTransform transform = new RecordsTransform(this.changes, this.deletions, this.additions);
        this.session.updateData(this.key, this.dataType, transform);
    }

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        return "Data update packet with: " + this.changes.size() + " changes, " + this.deletions.size() + " deletions and " + this.additions.size() + " additions.";
    }
}
