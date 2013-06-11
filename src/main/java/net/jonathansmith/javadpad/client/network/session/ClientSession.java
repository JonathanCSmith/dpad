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
package net.jonathansmith.javadpad.client.network.session;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.Engine;

/**
 *
 * @author Jon
 */
public class ClientSession extends Session {
    
    public ClientSession(Engine eng, Channel c) {
        super(eng, c);
    }

    @Override
    public void addPacketToSend(PacketPriority priority, Packet p) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void addPacketToReceive(PacketPriority priority, Packet p) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }
}
