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

import java.util.Arrays;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.Session.State;
import net.jonathansmith.javadpad.common.security.SecurityHandler;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

import java.security.SecureRandom;

/**
 *
 * @author Jon
 */
public class HandshakePacket extends Packet {
    
    public String version = "";
    
    private static final SecureRandom random = new SecureRandom();
    
    public HandshakePacket() {
        super();
    }
    
    public HandshakePacket(Engine engine, Session session) {
        super(engine, session);
    }

    @Override
    public int getNumberOfPayloads() {
        return 1;
    }

    @Override
    public int[] getPayloadSizes() {
        return new int[] {this.engine.getVersion().getBytes().length};
    }

    @Override
    public byte[] writePayload(int payloadNumber) {
        if (payloadNumber != 0) {
            System.out.println("Encode failure, payloads are being read wrong");
            return null;
        }
        
        else {
            return this.engine.getVersion().getBytes();
        }
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        if (payloadNumber != 0) {
            System.out.println("Decode failure, payloads are being read wrong");
        }
        
        else {
            this.version = Arrays.toString(bytes);
        }
    }

    @Override
    public void handleClientSide() {
        throw new RuntimeException("Packet does not have client side action");
    }

    @Override
    public void handleServerSide() {
        if (this.session.getState() != State.EXCHANGING_HANDSHAKE) {
            System.out.println("Invalid handshake packet received");
        }
        
        if (!this.engine.getVersion().contentEquals(this.version)) {
            this.session.disconnect(); // TODO: Reason?
        }
        
        // TODO Encryption
        byte[] randomByte = new byte[4];
        random.nextBytes(randomByte);
        ((ServerSession) this.session).setVerifyToken(randomByte);
        
        AsymmetricCipherKeyPair keys = SecurityHandler.getInstance().getKeyPair();
        byte[] secret = SecurityHandler.getInstance().encodeKey(keys.getPublic());
        
        Packet p = new EncryptionKeyRequestPacket(this.engine, this.session, secret, randomByte);
        this.session.addPacketToSend(PacketPriority.CRITICAL, p);
    }
    
    static {
        synchronized(random) {
            random.nextBytes(new byte[1]);
        }
    }
}
