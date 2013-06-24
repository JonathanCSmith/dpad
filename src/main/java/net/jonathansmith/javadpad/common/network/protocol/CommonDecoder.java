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
import org.jboss.netty.channel.ChannelHandlerContext;

import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.util.logging.exceptions.DecoderException;

/**
 *
 * @author jonathansmith
 */
public class CommonDecoder extends StateDrivenDecoder<CommonDecoder.DecodingState> {
    
    private int type;
    private PacketPriority priority;
    private int paramCount;
    private int paramSize;
    private int frameRead = 0;
    private int largePayloadFrame = 0;
    private byte[] largePayloadStoredInformation;
    private byte[] largePayloadBuffer = new byte[8192];
    private Packet packet;
 
    public CommonDecoder() {
        super(DecodingState.TYPE);
    }
 
    @Override
    protected void cleanup() {
        this.type = -1;
        this.paramCount = 0;
        this.paramSize = 0;
        this.frameRead = -1;
        this.packet = null;
        
        // Large
        this.largePayloadFrame = 0;
        this.largePayloadStoredInformation = null;
    }

    @Override
    protected DecodeResult decode(ChannelHandlerContext ctx, ChannelBuffer buffer, DecodingState currentState) throws Exception {
        switch (currentState) {
            case TYPE:
                this.type = buffer.readInt();
                this.packet = Packet.getPacket(this.type).newInstance();
                
                return this.continueDecoding(DecodingState.PRIORITY);
                
            case PRIORITY:
                this.priority = PacketPriority.getPriority(buffer.readByte());
                return this.continueDecoding(DecodingState.PARAM_COUNT);
 
            case PARAM_COUNT:
                this.paramCount = buffer.readInt();
                
                if (this.paramCount > 0) {
                    this.frameRead = 0;
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                }
                
                else {
                    return this.finishedDecoding(new PacketMessage(this.packet, this.priority));
                }
 
            case PARAM_SIZE:
                this.paramSize = buffer.readInt();
                if (this.paramSize == 0) {
                    this.frameRead++;
                    if (this.frameRead >= this.paramCount) {
                        return this.finishedDecoding(new PacketMessage(this.packet, this.priority));
                    }
                    
                    else {
                        System.out.println("For packet: " + this.packet.getClass().toString());
                        System.out.println("Skipping payload number: " + (this.frameRead - 1));
                        System.out.println("As it declared and empty payload");
                        return this.continueDecoding(DecodingState.PARAM_SIZE);
                    }
                }
                
                else {
                    return this.continueDecoding(DecodingState.PARAM_VALUE);
                }
 
            case PARAM_VALUE:
                if (this.paramSize <= 8192) {
                    try {
                        byte[] currentPayload = new byte[this.paramSize];
                        buffer.readBytes(currentPayload);
                        this.packet.parsePayload(this.frameRead, currentPayload);
                    }

                    catch (Exception e) {
                        throw new DecoderException("Packet: " + this.packet.getClass().toString() + " is having issues when decoding! Payload number was: " + this.frameRead, e);
                    }
 
                    this.frameRead++;
                    if (this.frameRead >= this.paramCount) {
                        return this.finishedDecoding(new PacketMessage(this.packet, this.priority));
                    }

                    else {
                        return this.continueDecoding(DecodingState.PARAM_SIZE);
                    }
                }
                
                else {
                    this.largePayloadStoredInformation = new byte[this.paramSize];
                    this.largePayloadFrame = 1;
                    buffer.readBytes(this.largePayloadBuffer);
                    System.arraycopy(this.largePayloadBuffer, 0, this.largePayloadStoredInformation, 0, 8192);
                    
                    return this.continueDecoding(DecodingState.PARAM_VALUE_CONTINUED);
                }
                
            case PARAM_VALUE_CONTINUED:
                if ((this.largePayloadFrame +  1) * 8192 < this.paramSize) {
                    buffer.readBytes(this.largePayloadBuffer);
                    System.arraycopy(this.largePayloadBuffer, 0, this.largePayloadStoredInformation, this.largePayloadFrame * 8192, 8192);
                    this.largePayloadFrame++;
                    return this.continueDecoding(DecodingState.PARAM_VALUE_CONTINUED);
                }
                
                else {
                    byte[] finalChunk = new byte[this.paramSize - (this.largePayloadFrame * 8192)];
                    buffer.readBytes(finalChunk);
                    System.arraycopy(finalChunk, 0, this.largePayloadStoredInformation, this.largePayloadFrame * 8192, finalChunk.length);
                    this.packet.parsePayload(this.frameRead, this.largePayloadStoredInformation);
                    
                    if (this.frameRead >= this.paramCount) {
                        return this.finishedDecoding(new PacketMessage(this.packet, this.priority));
                    }

                    else {
                        return this.continueDecoding(DecodingState.PARAM_SIZE);
                    }
                }
                
            default:
                throw new IllegalStateException("Unknown state: " + currentState);
        }
    }
 
    // public classes --------------------------------------------------------
 
    public static enum DecodingState {
        TYPE,
        PRIORITY,
        PARAM_COUNT,
        PARAM_SIZE,
        PARAM_VALUE,
        PARAM_VALUE_CONTINUED;
    }
}
