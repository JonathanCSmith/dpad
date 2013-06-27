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

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;


/**
 *
 * @author Jon
 */
public class PluginStatusPacket extends LockedPacket {

    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private int status;
    
    public PluginStatusPacket() {
        super();
    }
    
    public PluginStatusPacket(Engine engine, Session session, int status) {
        super(engine, session);
        this.status = status;
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
        return 1;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        return 4;
    }

    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        return new byte[] {(byte) (this.status >> 24), (byte) (this.status >> 16), (byte) (this.status >> 8), (byte) this.status};
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        this.status = bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
    
    @Override
    public void handleClientSide() {
        RecordsList<Record> data = new RecordsList<Record> ();
        data.add(new IntegerRecord(this.status));
        this.session.addData(this.getKey(), SessionData.PLUGIN_STATUS, data);
    }

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        return "Plugin status with status of: " + this.status;
    }
}
