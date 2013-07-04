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
public class PluginUploadRequestPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private byte status;
    private PluginRecord record;
    private byte[] data = null;
    
    public PluginUploadRequestPacket() {
        super();
    }
    
    public PluginUploadRequestPacket(Engine engine, Session session, byte status, PluginRecord record) {
        super(engine, session);
        this.status = status;
        this.record = record;
        
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
                return this.data.length;
                
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
        }
    }

    @Override
    public void handleClientSide() {
        RecordsList<Record> data = new RecordsList<Record> ();
        data.add(new IntegerRecord(this.status));
        ((ClientSession) this.session).setSessionData(this.getKey(), SessionData.PLUGIN_STATUS, data);
    }

    @Override
    public void handleServerSide() {
        ((ServerSession) this.session).handleNewPluginRequest(false, this.record);
        //((ServerSession) this.session).uploadPlugin(SessionData.PLUGIN, this.record);
        // TODO: Fix the above method and load the file
    }

    @Override
    public String toString() {
        return "Plugin upload request packet";
    }
}