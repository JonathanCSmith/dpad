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

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

/**
 *
 * @author Jon
 */
public class DisconnectPacket extends LockedPacket {

    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private boolean kicked;
    
    public DisconnectPacket() {
        super();
    }
    
    public DisconnectPacket(Engine engine, Session session, boolean kicked) {
        super(engine, session);
        this.kicked = kicked;
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
                return this.key.getBytes().length;
                
            case 1:
                return 1;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writePayload(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return this.key.getBytes();
                
            case 1:
                byte[] out = new byte[1];
                if (!this.kicked) {
                    out[0] = 0;
                }
                
                else {
                    out[0] = 1;
                }
                
                return out;
                
            default:
                return null;
        }
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.key = new String(bytes);
                return;
                
            case 1:
                if (bytes[0] == 0) {
                    this.kicked = false;
                }
                
                else {
                    this.kicked = true;
                }
                return;
        }
    }

    @Override
    public void handleClientSide() {
        ((Client) this.engine).setDisconnectExpected();
    }

    @Override
    public void handleServerSide() {
        ((ServerSession) this.session).setDisconnectExpected();
    }

    @Override
    public String toString() {
        return "Disconnect packet where the player was kicked == " + this.kicked;
    }
}
