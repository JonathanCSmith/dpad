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

/**
 *
 * @author jonathansmith
 */
public class CommonDecoder extends StateDrivenDecoder {
    
    private int type;
    private byte[] id;
    private int nParams;
    private List<string> params;
    private byte[] param;
 
    // constructors ----------------------------------------------------------
 
    public CommonDecoder() {
        super(DecodingState.TYPE);
    }
 
    @Override
    protected void cleanup() {
        // cleanup pending resources allocated for decoding
        this.id = null;
        this.nParams = 0;
        this.type = -1;
        this.param = null;
        this.params = null;
    }

    @Override
    protected DecodeResult decode(ChannelBuffer buffer, Enum currentState) throws Exception {
        switch (currentState) {
            case TYPE:
                this.type = buffer.readInt();
                return this.continueDecoding(DecodingState.ID_SIZE);
 
            case ID_SIZE:
                // Should be protected for 0 or negative sizes.
                this.id = new byte[buffer.readByte()];
                return this.continueDecoding(DecodingState.ID);
 
            case ID:
                buffer.readBytes(this.id);
                if (this.type == 1) {
                    // Lets assume type 1 messages only need id.
                    Message m = new Type1Message(new String(this.id));
                    return this.finishedDecoding(m);
                } else {
                    // Otherwise continue decoding.
                    return this.continueDecoding(DecodingState.PARAM_COUNT);
                }
 
            case PARAM_COUNT:
                this.nParams = buffer.readByte();
                // If there are parameters continue decoding, otherwise bail.
                if (this.nParams > 0) {
                    this.params = new ArrayList<string>(this.nParams);
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                } else {
                    Message m = new OtherMessage(new String(this.id));
                    return this.finishedDecoding(m);
                }
 
            case PARAM_SIZE:
                this.param = new byte[buffer.readByte()];
                return this.continueDecoding(DecodingState.PARAM_VALUE);
 
            case PARAM_VALUE:
                buffer.readBytes(this.param);
                this.params.add(new String(this.param));
                if (this.params.size() >= this.nParams) {
                    // This was the last parameter, exit.
                    Message m = new OtherMessage(new String(this.id));
                    m.setParams(this.params);
                    return this.finishedDecoding(m);
                } else {
                    // Continue reading parameters.
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                }
 
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
    }
 
    // public classes --------------------------------------------------------
 
    public static enum DecodingState {
        TYPE,
        ID_SIZE,
        ID,
        PARAM_COUNT,
        PARAM_SIZE,
        PARAM_VALUE
    }
}
