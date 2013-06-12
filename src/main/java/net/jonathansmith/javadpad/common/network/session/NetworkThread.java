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
package net.jonathansmith.javadpad.common.network.session;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;

/**
 *
 * @author Jon
 */
public abstract class NetworkThread extends Thread {
    
    public final ListMultimap<PacketPriority, Packet> packets = Multimaps.synchronizedListMultimap(Multimaps.newListMultimap(Maps.<PacketPriority, Collection<Packet>> newEnumMap(PacketPriority.class), new Supplier<List<Packet>> () {
        @Override
        public List<Packet> get() { 
            return Lists.newArrayList();
        }
    }));
    
    public final Engine engine;
    public final Session session;
    
    private boolean shouldShutdown = false;
    
    public NetworkThread(Engine engine, Session sess) {
        this.engine = engine;
        this.session = sess;
    }
    
    public void addPacket(PacketPriority priority, Packet packet) {
        this.packets.put(priority, packet);
    }
    
    public void sendPacket(PacketMessage pm) {
        this.session.sendPacketMessage(pm);
    }
    
    @Override
    public abstract void run();
    
    public boolean isRunning() {
        return !this.shouldShutdown;
    }
    
    public final void shutdown() {
        this.shouldShutdown = true;
    }
}
