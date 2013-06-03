/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.aaaarewrite.common.network.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;

/**
 *
 * @author jonathansmith
 */
public class CommonEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext chc, Channel chnl, Object o) throws Exception {
        if (o instanceof Packet) {
            Packet p = (Packet) o;
            int size = p.getPacketLength();
            ChannelBuffer header = ChannelBuffers.buffer(8 + size);
            header.writeInt(p.getID());
            header = p.writePayload(header);
            return ChannelBuffers.wrappedBuffer(header);
        }
        
        return o;
    }
}
