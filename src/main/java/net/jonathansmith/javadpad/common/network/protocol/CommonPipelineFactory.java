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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import net.jonathansmith.javadpad.common.Engine;

/**
 *
 * @author jonathansmith
 */
public class CommonPipelineFactory implements ChannelPipelineFactory {
    private final Engine engine;

    public CommonPipelineFactory(Engine engine) {
        this.engine = engine;
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        CommonLogger logger = new CommonLogger(this.engine);
        CommonDecoder decoder = new CommonDecoder();
        CommonEncoder encoder = new CommonEncoder();
        CommonHandler handler = new CommonHandler(this.engine);
        
        return Channels.pipeline(encoder, decoder, logger, handler);
    }
}
