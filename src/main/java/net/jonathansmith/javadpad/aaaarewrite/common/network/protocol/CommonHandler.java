/*
 * Copyright (C) 2013 Jon
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

import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import net.jonathansmith.javadpad.aaaarewrite.common.thread.MonitoredThread;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

/**
 *
 * @author Jon
 */
public class CommonHandler extends SimpleChannelUpstreamHandler {
    
    private final CommonEncoder encoder;
    private final CommonDecoder decoder;
    private final MonitoredThread engine;
    private final boolean upstream;
    
    public CommonHandler(boolean up, MonitoredThread eng, CommonEncoder enc, CommonDecoder dec) {
        this.encoder = enc;
        this.decoder = dec;
        this.engine = eng;
        this.upstream = up;
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent evt) {
        Packet packet = this.decoder.d
    }
}
