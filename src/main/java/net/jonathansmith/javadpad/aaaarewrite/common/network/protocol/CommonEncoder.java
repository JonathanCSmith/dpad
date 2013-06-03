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
    
    private boolean upstream;
    
    public CommonEncoder(boolean upstream) {
        this.upstream = upstream;
    }

    @Override
    protected Object encode(ChannelHandlerContext chc, Channel chnl, Object o) throws Exception {
        if (o instanceof Packet) {
            Packet p = (Packet) o;
            ChannelBuffer header = p.writeHeader(this.upstream);
            ChannelBuffer msg = p.writePayload(this.upstream, header);
            return ChannelBuffers.wrappedBuffer(header, msg);
        }
        
        return o;
    }
}
