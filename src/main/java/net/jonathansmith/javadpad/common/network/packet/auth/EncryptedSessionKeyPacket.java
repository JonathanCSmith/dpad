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
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;

/**
 *
 * @author Jon
 */
public class EncryptedSessionKeyPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    public EncryptedSessionKeyPacket() {
        super();
    }
    
    public EncryptedSessionKeyPacket(Engine engine, Session session) {
        super(engine, session);
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
        return 0;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        return 0;
    }

    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        return null;
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {}

    @Override
    public void handleClientSide() {
        this.session.handleSessionKey(this);
    }

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        return "Encrypted session key packet";
    }
}
