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

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.decoder.DecodeResult;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.decoder.RollingDecoder;

/**
 *
 * @author jonathansmith
 */
public class CommonDecoder extends RollingDecoder<CommonDecoder.DecodingState> {
    
    public static enum DecodingState {
        TYPE,
        PARAM_COUNT,
        PARAM_SIZE,
        PARAM_VALUE;
    }
    
    private Packet packet;
    
    private int type;
    private byte[] id;
    private int nParams;
    private List<String> params;
    private byte[] param;
    
    public CommonDecoder() {
        super(DecodingState.TYPE);
    }
    
    @Override
    protected DecodeResult<DecodingState> decode(ChannelBuffer buffer, DecodingState state) throws Exception {
        switch (state) {
            case TYPE:
                this.packet = Packet.getPacket(buffer.readInt());
                return this.continueDecoding(DecodingState.PARAM_COUNT);
                
            case PARAM_COUNT:
                this.nParams = buffer.readByte();
                
                if (this.nParams > 0) {
                    this.params = new ArrayList<String> (this.nParams);
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                }
                
                else {
                    Packet packet = Packet.getPacket(this.type);
                    return this.finishedDecoding(packet);
                }
                
            case PARAM_SIZE:
                this.param = new byte[buffer.readByte()];
                return this.continueDecoding(DecodingState.PARAM_VALUE);
                
            case PARAM_VALUE:
                buffer.readBytes(this.param);
                this.params.add(new String(this.param));
                if (this.params.size() >= this.nParams) {
                    
                    return this.finishedDecoding();
                }
                
                else {
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                }
                
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
    }
    
    @Override
    public void cleanup() {
        this.id = null;
        this.nParams = 0;
        this.type = -1;
        this.param = null;
        this.params = null;
    }
}
