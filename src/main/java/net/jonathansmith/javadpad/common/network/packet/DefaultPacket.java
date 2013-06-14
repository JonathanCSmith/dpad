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
package net.jonathansmith.javadpad.common.network.packet;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.session.Session;

/**
 *
 * @author Jon
 */
public class DefaultPacket extends Packet {
    
    private static int id;
    
    public DefaultPacket() {
        super();
    }
    
    public DefaultPacket(Engine engine, Session session) {
        super(engine, session);
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        if (newID != 0) {
            throw new RuntimeException("Default packet must have id 0, as it is the packet fallback");
        }
        
        id = newID;
    }

    @Override
    public int getNumberOfPayloads() {
        return 0;
    }
    
    @Override
    public int getPayloadSize(int payloadNumber) {
        return 0;
    }
    
    @Override
    public byte[] writePayload(int payloadNumber) {
        return null;
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {}

    @Override
    public void handleClientSide() {}

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        return "Blank packet: this is the packet fallback, if you are seeing this, it is likely that you forgot to register one of your packets!";
    }
    
}
