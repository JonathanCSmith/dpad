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

import org.jboss.netty.buffer.ChannelBuffer;

import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;
import net.jonathansmith.javadpad.aaaarewrite.common.network.session.Session;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;

/**
 *
 * @author Jon
 */
class EncryptionKeyRequestPacket extends Packet {

    public EncryptionKeyRequestPacket(Engine engine, Session session) {
    }

    @Override
    public int getNumberOfPayloads() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int[] getPayloadSizes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChannelBuffer writePayload(int payloadNumber, ChannelBuffer header) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handleClientSide() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handleServerSide() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
