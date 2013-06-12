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
package net.jonathansmith.javadpad.client.network.listeners;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
import net.jonathansmith.javadpad.common.network.protocol.CommonHandler;

/**
 *
 * @author Jon
 */
public class ClientConnectListener implements ChannelFutureListener {

    private final Client client;
    
    public ClientConnectListener(Client client) {
        this.client = client;
    }
    
    public void operationComplete(ChannelFuture cf) throws Exception {
        Channel channel = cf.getChannel();
        
        if (cf.isSuccess()) {
            CommonHandler handler = channel.getPipeline().get(CommonHandler.class);
            ClientSession session = new ClientSession(this.client, channel);
            handler.setSession(session);
            client.setSession(session);
            
            Packet p = new HandshakePacket(this.client, session);
            session.addPacketToSend(PacketPriority.CRITICAL, p);
            session.incrementState();
        }
    }
}
