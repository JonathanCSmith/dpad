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
package net.jonathansmith.javadpad.aaaarewrite.server;

import java.net.InetSocketAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import net.jonathansmith.javadpad.aaaarewrite.common.network.CommonPipelineFactory;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.MonitoredThread;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.NamedThreadFactory;
import net.jonathansmith.javadpad.aaaarewrite.server.channelhandlers.EchoServerHandler;

/**
 *
 * @author jonathansmith
 */
public class ServerMainThread extends MonitoredThread {
    
    private boolean isAlive = false;
    private boolean errored = false;
    
    private ServerBootstrap bootstrap;
    private ChannelGroup channelGroup;
    
    public ServerMainThread(String host, int port) {
        super(host, port);
    }

    @Override
    public void init() {
        this.bootstrap = new ServerBootstrap();
        this.channelGroup = new DefaultChannelGroup();
        // Create server gui
    }

    @Override
    public void run() {
        // Display server gui
        ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Boss", true));
        ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Worker", true));
        ChannelFactory factory = new NioServerSocketChannelFactory(boss, worker);
        this.bootstrap.setFactory(factory);
        this.bootstrap.setOption("child.tcpNoDelay", true);
        this.bootstrap.setOption("child.keepAlive", true);
        
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, false);
        this.bootstrap.setPipelineFactory(pipelineFactory);
        
        Channel acceptor = this.bootstrap.bind(new InetSocketAddress(this.hostName, this.portNumber));
        
        if (acceptor.isBound()) {
            this.channelGroup.add(acceptor);
            this.isAlive = true;
        }
        
        else {
            this.bootstrap.releaseExternalResources();
            // TODO: Fail notice
            return;
        }
        
        while (this.isAlive) {
            try {
                Thread.sleep(100);
                
            } catch (InterruptedException ex) {
                // TODO: Fail notice
            }
        }
    }

    @Override
    public boolean isRunning() {
        return this.isAlive;
    }

    @Override
    public boolean isViable() {
        return !this.errored;
    }

    @Override
    public void saveAndShutdown() {
        
    }
}
