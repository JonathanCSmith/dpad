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
package net.jonathansmith.javadpad.common.network.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import net.jonathansmith.javadpad.api.Platform;
import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.server.Server;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

/**
 *
 * @author Jon
 */
public class CommonHandler extends SimpleChannelUpstreamHandler {
    private final Engine engine;
    private Session session = null;
    
    public CommonHandler(Engine eng) {
        this.engine = eng;
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel c = e.getChannel();
        
        if (this.engine.platform == Platform.SERVER) {
            ((Server) this.engine).getChannelGroup().add(c);
            this.setSession(((Server) this.engine).getSessionRegistry().addAndGetNewSession(c));
            this.engine.info("Client connected");
        }
        
        else {
            this.engine.info("Connected to server");
        }
    }
    
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel c = e.getChannel();
        if (!this.engine.isAlive()) {
            return;
        }
        
        if (this.engine.platform == Platform.SERVER) {
            this.engine.info("Client disconnected");
            ((Server) this.engine).getChannelGroup().remove(c);
            ((Server) this.engine).getSessionRegistry().remove((ServerSession) this.session);
            this.session.disconnect(false);
        }
        
        else {
            this.engine.info("Disconnected from server!");
            ((Client) this.engine).channelDisconnect();
        }
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        PacketMessage p = (PacketMessage) e.getMessage();
        Packet packet = p.getPacket();
        
        // Packet exceptions...
        if (this.session == null) {
            return;
        }
        
        this.session.addPacketToReceive(p.getPriority(), packet);
        super.messageReceived(ctx, e);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        this.engine.error("Exception caught in: " + this.engine.platform.toString().toLowerCase() + " CommonHandler", e.getCause());
    }
    
    public void setSession(Session session) {
        if (this.session == null) {
            this.session = session;
        }
        
        else {
            this.engine.error("Cannot have multiple sessions, a modification was attempted!");
        }
    }
}
