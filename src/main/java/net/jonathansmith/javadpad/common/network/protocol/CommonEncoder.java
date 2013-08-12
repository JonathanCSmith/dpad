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

import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;

import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.LargePayloadPacket;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;

/**
 *
 * @author jonathansmith
 */
public class CommonEncoder implements ChannelDownstreamHandler {

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) {
        if (!(evt instanceof MessageEvent)) {
            ctx.sendDownstream(evt);
            return;
        }
        
        MessageEvent e = (MessageEvent) evt;
        if (e.getMessage() instanceof PacketMessage) {
            PacketMessage msg = (PacketMessage) e.getMessage();
            Packet p = msg.getPacket();
            PacketPriority priority = msg.getPriority();
            ChannelBuffer header = this.encodePacketHeader(priority, p);
            Channels.write(ctx, e.getFuture(), header, e.getRemoteAddress());
            
            // Horrible looking if else block used to reduce instanceof checks
            if (p instanceof LargePayloadPacket) {
                if (p instanceof LockedPacket) {
                    // Special case of locked + large, because large is an interface, we cannot let it handle this itself
                    for (int i = 0; i < p.getNumberOfPayloads(); i++) {
                        if (i == 0) {
                            this.writePayload(ctx, e.getFuture(), e.getRemoteAddress(), p, i);
                        }
                        
                        else {
                            if (((LargePayloadPacket) p).isPayloadLarge(i - 1)) {
                                this.writePayloadSlowly(ctx, e.getFuture(), e.getRemoteAddress(), (LargePayloadPacket) p, i - 1);
                            }
                            
                            else {
                                this.writePayload(ctx, e.getFuture(), e.getRemoteAddress(), p, i);
                            }
                        }
                    }
                }
                
                else {
                    // Just a large packet, normal handling here
                    for (int i = 0; i < p.getNumberOfPayloads(); i++) {
                        if (((LargePayloadPacket) p).isPayloadLarge(i)) {
                            this.writePayloadSlowly(ctx, e.getFuture(), e.getRemoteAddress(), (LargePayloadPacket) p, i);
                        }
                        
                        else {
                            this.writePayload(ctx, e.getFuture(), e.getRemoteAddress(), p, i);
                        }
                    }
                }
            }
            
            else {
                // Normal packet, this is the most common scenario
                for (int i = 0; i < p.getNumberOfPayloads(); i++) {
                    this.writePayload(ctx, e.getFuture(), e.getRemoteAddress(), p, i);
                }
            }
            
        } else {
            ctx.sendDownstream(evt);
        }
    }
    
    protected ChannelBuffer encodePacketHeader(PacketPriority priority, Packet p) {
        ChannelBuffer buff = ChannelBuffers.buffer(4 + 1 + 4);
        buff.writeInt(p.getID());
        buff.writeByte(priority.ordinal());
        buff.writeInt(p.getNumberOfPayloads());
        return buff;
    }
    
    protected void writePayload(ChannelHandlerContext ctx, ChannelFuture f, SocketAddress address, Packet p, int payloadNumber) {
        int payloadSize = p.getPayloadSize(payloadNumber);
        byte[] payload = p.writePayload(payloadNumber);
        
        ChannelBuffer sizeBuff = ChannelBuffers.buffer(4);
        sizeBuff.writeInt(payloadSize);
        Channels.write(ctx, f, sizeBuff, address);
        
        if (payloadSize > 0 && payloadSize <= 8192) {
            ChannelBuffer buff = ChannelBuffers.buffer(payloadSize);
            buff.writeBytes(payload);
            Channels.write(ctx, f, buff, address);
        }
        
        else if (payloadSize > 0) {
            ChannelBuffer buff = ChannelBuffers.buffer(8192);
            byte[] temp = new byte[8192];
            int length = 0;
            
            while (length < payloadSize && length + 8192 <= payloadSize) {
                System.arraycopy(payload, length, temp, 0, 8192);
                buff.writeBytes(temp);
                Channels.write(ctx, f, buff, address);
                buff.clear();
                length += 8192;
            }
            
            int finalChunkSize = payloadSize - length;
            byte[] finalChunk = new byte[finalChunkSize];
            System.arraycopy(payload, length, finalChunk, 0, finalChunkSize);
            ChannelBuffer rem = ChannelBuffers.buffer(finalChunkSize);
            rem.writeBytes(finalChunk);
            Channels.write(ctx, f, rem, address);
        }
    }

    protected void writePayloadSlowly(ChannelHandlerContext ctx, ChannelFuture f, SocketAddress address, LargePayloadPacket p, int payloadNumber) {
        double payloadSize = p.getLargePayloadSize(payloadNumber);
        int wholeChunks = (int) Math.floor(payloadSize / (double) 8192);
        double finalChunkSize = payloadSize - (wholeChunks * 8192);
        ChannelBuffer buff = ChannelBuffers.buffer(8192);
        byte[] chunk = new byte[8192];
        byte[] finalChunk = new byte[(int) finalChunkSize];
        
        ChannelBuffer sizeBuff = ChannelBuffers.buffer(8);
        sizeBuff.writeDouble(payloadSize);
        Channels.write(ctx, f, sizeBuff, address);
        
        for (int i = 0; i < wholeChunks; i++) {
            p.writeLargePayloadFragment(payloadNumber, chunk, i);
            buff.writeBytes(chunk);
            Channels.write(ctx, f, buff, address);
            buff.clear();
        }
        
        p.writeLargePayloadFragment(payloadNumber, finalChunk, wholeChunks);
        ChannelBuffer rem = ChannelBuffers.buffer((int) finalChunkSize);
        rem.writeBytes(finalChunk);
        Channels.write(ctx, f, rem, address);
        
        p.finishWriting();
    }
}