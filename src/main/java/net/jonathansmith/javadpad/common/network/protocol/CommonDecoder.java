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

import org.bouncycastle.crypto.modes.CFBBlockCipher;

import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;

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
    private Packet packet;
    private CFBBlockCipher decrypter = null;
 
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
                return this.continueDecoding(DecodingState.PARAM_VALUE);
 
            case PARAM_VALUE:
                byte[] currentPayload = new byte[this.paramSize];
                byte[] outputPayload = new byte[this.paramSize];
                buffer.readBytes(currentPayload);
                
                if (this.decrypter != null && !this.packet.getIsUnencrypted()) {
                    this.decrypter.decryptBlock(currentPayload, 0, outputPayload, 0);
                    this.packet.parsePayload(this.frameRead, outputPayload);
                }
                
                else {
                    this.packet.parsePayload(this.frameRead, currentPayload);
                }
                this.frameRead++;
                
                if (this.frameRead >= this.paramCount) {
                    return this.finishedDecoding(new PacketMessage(this.packet, this.priority));
                }
                
                else {
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                }
 
            default:
                throw new IllegalStateException("Unknown state: " + currentState);
        }
    }
    
    public void setDecryption(CFBBlockCipher decryption) {
        this.decrypter = decryption;
    }
 
    // public classes --------------------------------------------------------
 
    public static enum DecodingState {
        TYPE,
        PRIORITY,
        PARAM_COUNT,
        PARAM_SIZE,
        PARAM_VALUE
    }
}
