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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;

/**
 *
 * @author Jon
 */
public class CommonDownstreamLogger extends SimpleChannelDownstreamHandler {
    
    private final Engine engine;
    
    public CommonDownstreamLogger(Engine engine) {
        this.engine = engine;
    }
    
    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!(e instanceof PacketMessage)) {
            return;
        }
        
        PacketMessage message = (PacketMessage) e;
        
        this.engine.info("Packet being sent from: " + this.engine.platform.toString().toLowerCase());
        this.engine.info("Packet has priority: " + message.getPriority().toString().toLowerCase());
        this.engine.info("Packet has id: " + message.getPacket().getID());
        this.engine.info("Packet descriptor is: " + message.getPacket().toString());
        
        super.writeRequested(ctx, e);
    }
}
