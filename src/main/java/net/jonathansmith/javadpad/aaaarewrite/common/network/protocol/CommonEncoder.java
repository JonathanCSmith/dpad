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

import org.bouncycastle.crypto.modes.CFBBlockCipher;

import net.jonathansmith.javadpad.aaaarewrite.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;
import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.PacketPriority;

/**
 *
 * @author jonathansmith
 */
public class CommonEncoder extends OneToOneEncoder {
    
    private CFBBlockCipher encrypter = null;

    @Override
    protected Object encode(ChannelHandlerContext chc, Channel chnl, Object o) throws Exception {
        if (o instanceof PacketMessage) {
            Packet p = ((PacketMessage) o).getPacket();
            PacketPriority priority = ((PacketMessage) o).getPriority();
            
            int[] packetPayloadSizes = p.getPayloadSizes();
            int size = 0;
            int numberOfPayloads = p.getNumberOfPayloads();
            for (Integer i : packetPayloadSizes) {
                size += 8 + i;
            }
            
            ChannelBuffer buff = ChannelBuffers.buffer(8 + 1 + 8 + size);
            buff.writeInt(p.getID());
            buff.writeByte(priority.ordinal());
            buff.writeInt(numberOfPayloads);
            
            for (int i = 0; i < numberOfPayloads; i++) {
                buff.writeInt(packetPayloadSizes[i]);
                buff = p.writePayload(i, buff, encrypter);
            }
            
            return ChannelBuffers.wrappedBuffer(buff);
        }
        
        return o;
    }
    
    public void setEncryption(CFBBlockCipher cipher) {
        this.encrypter = cipher;
    }
}
