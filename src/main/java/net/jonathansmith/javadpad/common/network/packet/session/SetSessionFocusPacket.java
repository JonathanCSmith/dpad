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
package net.jonathansmith.javadpad.common.network.packet.session;

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

/**
 *
 * @author Jon
 */
public class SetSessionFocusPacket extends LockedPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private SessionData dataType;
    
    public SetSessionFocusPacket() {
        super();
    }
    
    public SetSessionFocusPacket(Engine engine, Session session, SessionData dataType) {
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
    public int getNumberOfLockedPayloads() {
        return 1;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        return payloadNumber == 0 ? 1 : 0;
    }

    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        return payloadNumber == 0 ? new byte[] {(byte) this.dataType.ordinal()} : null;
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        if (payloadNumber == 0) {
            this.dataType = SessionData.values()[bytes[0]];
        }
    }

    @Override
    public void handleClientSide() {
        RecordsList<Record> list = this.session.checkoutSessionData(this.getKey(), this.dataType);
        ((ClientSession) this.session).setSessionData(this.getKey(), this.dataType, list);
    }

    @Override
    public void handleServerSide() {
        RecordsList<Record> list = this.session.checkoutSessionData(this.getKey(), this.dataType);
        ((ServerSession) this.session).setSessionData(this.getKey(), this.dataType, list);
    }

    @Override
    public String toString() {
        return "Set session focus packet for: " + this.dataType.toString().toLowerCase();
    }
}