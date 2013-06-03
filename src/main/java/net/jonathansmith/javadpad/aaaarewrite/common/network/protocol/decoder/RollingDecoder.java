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
package net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.decoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

/**
 *
 * @author jonathansmith
 */
public abstract class RollingDecoder<T extends Enum<T>> extends ReplayingDecoder<T> {
    
    private final T state;
    
    public RollingDecoder(T initialState) {
        this(initialState, false);
    }
    
    public RollingDecoder(T initialState, boolean unfold) {
        super(initialState, unfold);
        this.state = initialState;
    }

    @Override
    protected Object decode(ChannelHandlerContext chc, Channel chnl, ChannelBuffer cb, T t) throws Exception {
        for(;;) {
            DecodeResult<T> result = this.decode(cb, this.getState());
            if (result == null) {
                throw new IllegalArgumentException("decode returned null");
            }
            
            switch (result.getType()) {
                case FINISHED:
                    try {
                        return ((FinishedDecodeResult) result).getResult();
                    }
                    
                    finally {
                        this.reset();
                    }
                    
                case CONTINUE:
                    this.checkpoint(((ContinueDecodeResult<T>) result).getNextState());
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unsupported Result! " + result.getType());
            }
        }
    }
    
    protected DecodeResult<T> continueDecoding(T nextState) {
        return new ContinueDecodeResult<T> (nextState);
    }
    
    protected DecodeResult<T> finishedDecoding(Object result) {
        return new FinishedDecodeResult<T> (result);
    }
    
    protected void reset() {
        this.cleanup();
        this.setState(this.state);
    }
    
    protected abstract DecodeResult<T> decode(ChannelBuffer buff, T currentState) throws Exception;
    
    protected abstract void cleanup();
}
