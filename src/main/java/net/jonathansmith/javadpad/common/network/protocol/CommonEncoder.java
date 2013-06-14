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
package net.jonathansmith.javadpad.common.network.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;

/**
 *
 * @author jonathansmith
 */
public class CommonEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext chc, Channel chnl, Object o) throws Exception {
        if (o instanceof PacketMessage) {
            Packet p = ((PacketMessage) o).getPacket();
            PacketPriority priority = ((PacketMessage) o).getPriority();
            
            int numberOfPayloads = p.getNumberOfPayloads();
            int[] packetPayloadSizes = new int[numberOfPayloads];
            int size = 0;
            
            if (numberOfPayloads != 0) {
                for (int i = 0; i < numberOfPayloads; i++) {
                    packetPayloadSizes[i] = p.getPayloadSize(i);
                    size += 4 + packetPayloadSizes[i];
                }
            }
            
            ChannelBuffer buff = ChannelBuffers.buffer(4 + 1 + 4 + size);
            buff.writeInt(p.getID());
            buff.writeByte(priority.ordinal());
            buff.writeInt(numberOfPayloads);
            
            if (numberOfPayloads != 0) {
                for (int i = 0; i < numberOfPayloads; i++) {
                    buff.writeInt(packetPayloadSizes[i]);
                    if (packetPayloadSizes[i] == 0) {
                        continue;
                    }
                    
                    byte[] currentPayload = p.writePayload(i);
                    buff.writeBytes(currentPayload);
                }
            }
            
            return ChannelBuffers.wrappedBuffer(buff);
        }
        
        return o;
    }
}
