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

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.session.Session;

/**
 *
 * @author Jon
 */
public abstract class LockedPacket extends Packet{
    
    private final AtomicBoolean lock = new AtomicBoolean(false);
    
    private String key;
    
    public LockedPacket() {
        super();
    }
    
    public LockedPacket(Engine engine, Session session) {
        super(engine, session);
    }
    
    public void lockPacket(String key) {
        if (this.lock.compareAndSet(false, true)) {
            this.key = key;
        }
    }
    
    public String getKey() {
        return this.key;
    }
    
    @Override
    public final int getNumberOfPayloads() {
        return this.getNumberOfLockedPayloads() + 1;
    }
    
    public abstract int getNumberOfLockedPayloads();
    
    @Override
    public final int getPayloadSize(int payloadNumber) {
        if (payloadNumber == 0) {
            return this.key.getBytes().length;
        }
        
        else {
            return this.getLockedPayloadSize(payloadNumber - 1);
        }
    }
    
    public abstract int getLockedPayloadSize(int payloadNumber);
    
    @Override
    public final byte[] writePayload(int payloadNumber) {
        if (payloadNumber == 0) {
            return this.key.getBytes();
        }
        
        else {
            return this.writeLockedPayload(payloadNumber - 1);
        }
    }
    
    public abstract byte[] writeLockedPayload(int payloadNumber);
    
    @Override
    public final void parsePayload(int payloadNumber, byte[] bytes) {
        if (payloadNumber == 0) {
            this.lockPacket(new String(bytes));
        }
        
        else {
            this.parseLockedPayload(payloadNumber - 1, bytes);
        }
    }
    
    public abstract void parseLockedPayload(int payloadNumber, byte[] bytes);
}
