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
    
    protected String key;
    
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
}
