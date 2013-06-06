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
package net.jonathansmith.javadpad.aaaarewrite.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import net.jonathansmith.javadpad.aaaarewrite.common.network.CommonPipelineFactory;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.NamedThreadFactory;

/**
 *
 * @author jonathansmith
 */
public class Client extends Engine {
    
    private boolean isAlive = false;
    private boolean errored = false;
    
    private ClientBootstrap bootstrap;
    private Channel channel;
    
    public Client(String host, int port) {
        super(host, port);
    }
    
    @Override
    public void init() {
        this.bootstrap = new ClientBootstrap();
        // Create gui
    }

    @Override
    public void run() {
        // Display gui
        ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Client - Boss", true));
        ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Client - Worker", true));
        ChannelFactory factory = new NioClientSocketChannelFactory(boss, worker);
        this.bootstrap.setFactory(factory);
        
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, true);
        this.bootstrap.setPipelineFactory(pipelineFactory);
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

    @Override
    public void forceShutdown() {
        
    }
}
