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
package net.jonathansmith.javadpad.common.network.packet.auth;

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.session.Session;

/**
 *
 * @author Jon
 */
public class EncryptedSessionKeyPacket extends Packet {
    
    private static int id;
    
    private final AtomicBoolean lock = new AtomicBoolean(false);
    
    private String sessionID;
    
    public EncryptedSessionKeyPacket() {
        super();
    }
    
    public EncryptedSessionKeyPacket(Engine engine, Session session, String id) {
        super(engine, session);
        this.sessionID = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        if (this.lock.compareAndSet(false, true)) {
            id = newID;
        }
    }

    @Override
    public int getNumberOfPayloads() {
        return 1;
    }

    @Override
    public int getPayloadSize(int payloadNumber) {
        if (payloadNumber != 0) {
            return 0;
        }
        
        return this.sessionID.getBytes().length;
    }

    @Override
    public byte[] writePayload(int payloadNumber, int providedSize) {
        if (payloadNumber != 0) {
            return null;
        }
        
        return this.sessionID.getBytes();
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        if (payloadNumber != 0) {
            return;
        }
        
        this.sessionID = new String(bytes);
    }

    @Override
    public void handleClientSide() {
        ((ClientSession) this.session).setKey(this.sessionID);
        this.session.incrementState();
    }

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        return "Encrypted session key packet";
    }
    
}
