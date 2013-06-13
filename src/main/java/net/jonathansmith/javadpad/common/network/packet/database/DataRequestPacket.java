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
import net.jonathansmith.javadpad.common.network.RequestType;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

/**
 *
 * @author Jon
 */
public class DataRequestPacket extends Packet {
    
    private static int id;
    
    private final AtomicBoolean lock = new AtomicBoolean(false);
    
    private RequestType dataType;
    
    public DataRequestPacket() {
        super();
    }
    
    public DataRequestPacket(Engine engine, Session session, RequestType dataType) {
        super(engine, session);
        this.dataType = dataType;
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
        return 1;
    }

    @Override
    public int getPayloadSize(int payloadNumber) {
        return 1;
    }

    @Override
    public byte[] writePayload(int payloadNumber, int providedSize) {
        byte[] returnVal = new byte[1];
        returnVal[0] = (byte) this.dataType.ordinal();
        return returnVal;
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        this.dataType = RequestType.values()[bytes[0]];
    }

    @Override
    public void handleClientSide() {}

    @Override
    public void handleServerSide() {
        ((ServerSession) this.session).checkoutData(this.dataType);
    }

    @Override
    public String toString() {
        return "Data request packet for: " + this.dataType.toString().toLowerCase();
    }
}
