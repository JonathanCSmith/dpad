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
package net.jonathansmith.javadpad.aaaarewrite.common.network.packet.auth;

import java.util.Arrays;

import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;
import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.PacketPriority;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.jonathansmith.javadpad.aaaarewrite.common.network.session.Session;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;

/**
 *
 * @author Jon
 */
public class HandshakePacket extends Packet {
    
    public String version = "";
    
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
    public ChannelBuffer writePayload(int payloadNumber, ChannelBuffer header) {
        if (payloadNumber != 0) {
            throw new RuntimeException("Encoder failure, invalid payload number");
        }
        
        else {
            return ChannelBuffers.wrappedBuffer(this.engine.getVersion().getBytes());
        }
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        if (payloadNumber != 0) {
            throw new RuntimeException("Decoder failure, invalid payload number");
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
        if (!this.engine.getVersion().contentEquals(this.version)) {
            this.session.disconnect(); // TODO: Reason?
        }
        
        // TODO Encryption
        Packet p = new EncryptionKeyRequestPacket(this.engine, this.session);
        this.session.addPacketToSend(PacketPriority.CRITICAL, p);
    }
}
