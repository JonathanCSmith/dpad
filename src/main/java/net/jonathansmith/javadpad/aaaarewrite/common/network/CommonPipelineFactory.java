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
package net.jonathansmith.javadpad.aaaarewrite.common.network;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import net.jonathansmith.javadpad.aaaarewrite.client.Client;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.CommonDecoder;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.CommonEncoder;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.CommonHandler;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;
import net.jonathansmith.javadpad.aaaarewrite.server.Server;

/**
 *
 * @author jonathansmith
 */
public class CommonPipelineFactory implements ChannelPipelineFactory {
    
    private final Engine engine;
    private final boolean upstream;

    public CommonPipelineFactory(Engine engine, boolean upstream) {
        if (upstream) {
            if (!(engine instanceof Client)) {
                throw new IllegalArgumentException("Only clients can establish upstream connections");
            }
        }
        
        else {
            if (!(engine instanceof Server)) {
                throw new IllegalArgumentException("Only servers can establish downstream connections");
            }
        }
        
        this.engine = engine;
        this.upstream = upstream;
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        CommonDecoder decoder = new CommonDecoder();
        CommonEncoder encoder = new CommonEncoder();
        CommonHandler handler = new CommonHandler(this.upstream, this.engine, encoder, decoder);
        return Channels.pipeline(decoder, encoder, handler);
    }
}
