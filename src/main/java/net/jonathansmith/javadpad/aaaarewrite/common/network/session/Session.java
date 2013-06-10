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
package net.jonathansmith.javadpad.aaaarewrite.common.network.session;

import java.util.Random;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;
import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;

/**
 *
 * @author Jon
 */
public abstract class Session {
    
    public final Channel channel;
    public final Engine engine;
    
    private final Random random = new Random();
    private final String id = Long.toString(random.nextLong(), 16).trim();
    
    public Session(Engine eng, Channel channel) {
        this.engine = eng;
        this.channel = channel;
    }

    public String getSessionID() {
        return this.id;
    }

    public abstract void addPacketToSend(PacketPriority priority, Packet p);
    
    public abstract void addPacketToReceive(PacketPriority priority, Packet p);
    
    public void sendPacket(Packet p) {
        this.channel.write(p);
    }

    public abstract void disconnect();
    
    public abstract void dispose();
}
