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

import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.CommonEncoder;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.MonitoredThread;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.CommonDecoder;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.CommonHandler;

/**
 *
 * @author jonathansmith
 */
public class CommonPipelineFactory implements ChannelPipelineFactory {
    
    private final MonitoredThread engine;
    private final boolean upstream;

    public CommonPipelineFactory(MonitoredThread engine, boolean upstream) {
        this.engine = engine;
        this.upstream = upstream;
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        CommonEncoder encoder = new CommonEncoder();
        CommonDecoder decoder = new CommonDecoder();
        CommonHandler handler = new CommonHandler(this.upstream, this.engine, encoder, decoder);
        return Channels.pipeline(encoder, decoder, handler);
    }
}
