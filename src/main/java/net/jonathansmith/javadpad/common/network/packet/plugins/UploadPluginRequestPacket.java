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
package net.jonathansmith.javadpad.common.network.packet.plugins;

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class UploadPluginRequestPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private byte status;
    private PluginRecord record;
    private boolean toServer;
    private byte[] data = null;
    
    public UploadPluginRequestPacket() {
        super();
    }
    
    public UploadPluginRequestPacket(Engine engine, Session session, byte status, PluginRecord record, boolean toServer) {
        super(engine, session);
        this.status = status;
        this.record = record;
        this.toServer = toServer;
        
        this.serializeData();
    }
    
    private void serializeData() {
        if (this.record == null) {
            return;
        }
        
        this.data = SerializationUtils.serialize(this.record);
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
        if (this.data == null) {
            return 2;
        }
        
        return 3;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return 1;
                
            case 1:
                return this.data.length;
                
            case 2:
                return 1;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return new byte[] {this.status};
                
            case 1:
                return this.data;
                
            case 2:
                if (this.toServer) {
                    return new byte[] {1};
                }
                
                else {
                    return new byte[] {0};
                }
                
            default:
                return null;
        }
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.status = bytes[0];
                break;
                
            case 1:
                this.record = (PluginRecord) SerializationUtils.deserialize(bytes);
                break;
                
            case 2:
                if (bytes[0] == 1) {
                    this.toServer = true;
                }
                
                else {
                    this.toServer = false;
                }
                break;
        }
    }

    @Override
    public void handleClientSide() {
        if (toServer) {
            RecordsList<Record> d = new RecordsList<Record> ();
            d.add(new IntegerRecord(this.status));
            ((ClientSession) this.session).setSessionData(this.getKey(), SessionData.PLUGIN_STATUS, d);
        }
        
        else {
            
        }
    }

    @Override
    public void handleServerSide() {
        ((ServerSession) this.session).handleUploadPluginRequest(this.toServer, false, this.record);
    }

    @Override
    public String toString() {
        return "Plugin upload request packet";
    }
}