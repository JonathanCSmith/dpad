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
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;

/**
 *
 * @author jonathansmith
 */
public abstract class StateDrivenDecoder<t extends Enum<t>> extends ReplayingDecoder<t> {
    
    protected Packet result;
    
    private final t initialState;
    
    public StateDrivenDecoder(t initialState) {
        this(initialState, false);
    }
    
    public StateDrivenDecoder(t initialState, boolean unfold) {
        super(initialState, unfold);
        this.initialState = initialState;
    }

    @Override
    protected Object decode(ChannelHandlerContext chc, Channel chnl, ChannelBuffer cb, Enum t) throws Exception {
        for (;;) {
            DecodeResult<t> outcome = this.decode(chc, cb, this.getState());
            if (outcome == null) {
                throw new IllegalArgumentException("Decode() returned null");
            }
            
            switch (outcome.getType()) {
                case FINISHED:
                    try {
                        return this.result;
                    }
                    
                    finally {
                        this.reset();
                    }
                    
                case CONTINUE:
                    this.checkpoint(((ContinueDecodeResult<t>) outcome).getNextState());
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unsupported result: " + outcome.getType());
            }
        }
    }
    
    protected DecodeResult<t> continueDecoding(t nextState) {
        return new ContinueDecodeResult<t>(nextState);
    }
 
    protected DecodeResult<t> finishedDecoding(PacketMessage p) {
        return new FinishedDecodeResult<t> (p);
    }
 
    protected void reset() {
        this.cleanup();
        this.setState(this.initialState);
    }
 
    protected abstract DecodeResult<t> decode(ChannelHandlerContext ctx, ChannelBuffer buffer, t currentState) throws Exception;
 
    protected abstract void cleanup();
    
    public interface DecodeResult<T> {
        enum Type {
            FINISHED,
            CONTINUE;
        }
        
        Type getType();
    }
    
    public class ContinueDecodeResult<T extends Enum<T>> implements DecodeResult<T> {
        
        private final T nextState;
        
        public ContinueDecodeResult(T nextState) {
            this.nextState = nextState;
        }
        
        @Override
        public Type getType() {
            return Type.CONTINUE;
        }
        
        public T getNextState() {
            return nextState;
        }
    }
    
    public class FinishedDecodeResult<T extends Enum<T>> implements DecodeResult<T> {
        
        private final PacketMessage packetMessage;
        
        public FinishedDecodeResult(PacketMessage p) {
            this.packetMessage = p;
        }
        
        @Override
        public Type getType() {
            return Type.FINISHED;
        }
        
        public PacketMessage getPacketMessage() {
            return this.packetMessage;
        }
    }
}
