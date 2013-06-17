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

/**
 *
 * @author Jon
 */
public class EncryptionKeyRequestPacket extends Packet {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);

    private static int id;
    
    public byte[] keys;
    public byte[] token;
    
    public EncryptionKeyRequestPacket() {
        super();
    }
    
    public EncryptionKeyRequestPacket(Engine engine, Session session, byte[] keys, byte[] token) {
        super(engine, session);
        this.keys = keys;
        this.token = token;
        this.forceUnencrypted(); // Asymmetric only for this packet as we havent established everything
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
        return 2;
    }

    @Override
    public int getPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return keys.length;
                
            case 1:
                return token.length;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writePayload(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return this.keys;
                
            case 1:
                return this.token;
                
            default:
                this.engine.warn("Encode failure, payloads are being read wrong");
                return null;
        }
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.keys = bytes;
                return;
                
            case 1:
                this.token = bytes;
                return;
                
            default:
                this.engine.warn("Decode failure, payloads are being read wrong");
                return;
        }
    }

    @Override
    public void handleClientSide() {
        this.session.handleEncryptionKeyRequest(this);
    }

    @Override
    public void handleServerSide() {}
    
    @Override
    public String toString() {
        return "Encryption key request packet";
    }
}
