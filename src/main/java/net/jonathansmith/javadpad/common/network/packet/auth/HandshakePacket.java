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

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.session.Session;

import java.security.SecureRandom;

/**
 *
 * @author Jon
 */
public class HandshakePacket extends Packet {
    
    public static final SecureRandom random = new SecureRandom();
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    public String version = "";
    
    public HandshakePacket() {
        super();
    }
    
    public HandshakePacket(Engine engine, Session session, String version) {
        super(engine, session);
        this.version = version;
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
        switch (payloadNumber) {
            case 0:
                return this.version.getBytes().length;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writePayload(int payloadNumber) {
        if (payloadNumber != 0) {
            this.engine.warn("Encode failure, payloads are being read wrong");
            return null;
        }
        
        else {
            return this.version.getBytes();
        }
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        if (payloadNumber != 0) {
            this.engine.warn("Decode failure, payloads are being read wrong");
        }
        
        else {
            this.version = new String(bytes);
        }
    }

    @Override
    public void handleClientSide() {}

    @Override
    public void handleServerSide() {
        this.session.handleHandshake(this);
    }
    
    @Override
    public String toString() {
        return "Handshake Packet";
    }
    
    static {
        synchronized(random) {
            random.nextBytes(new byte[1]);
        }
    }
}
