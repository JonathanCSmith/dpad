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
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import net.jonathansmith.javadpad.aaaarewrite.DPADNew;
import net.jonathansmith.javadpad.aaaarewrite.common.network.CommonPipelineFactory;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.NamedThreadFactory;
import net.jonathansmith.javadpad.aaaarewrite.server.network.session.SessionRegistry;

/**
 *
 * @author jonathansmith
 */
public class Server extends Engine {
    
    private final ChannelGroup channelGroup;
    private final SessionRegistry sessionRegistry;
    
    private ServerBootstrap bootstrap;
    
    public Server(DPADNew main, String host, int port) {
        super(main, host, port);
        this.channelGroup = new DefaultChannelGroup();
        this.sessionRegistry = new SessionRegistry(this);
        this.bootstrap = new ServerBootstrap();
        
        // TODO: FileSystem
    }

    @Override
    public void init() {
        // TODO: Initialise FileSystem
        // TODO: Config?
        // TODO: Console!
        // TODO: Gui, build + display (partial only?)
        // TODO: Connection pool
        // TODO: Default properties
        
        ChannelFactory factory;
        if (!this.hostName.contentEquals("local")) {
            ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Boss", true));
            ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Worker", true));
            factory = new NioServerSocketChannelFactory(boss, worker);
        }
        
        else {
            factory = new DefaultLocalServerChannelFactory();
        }
        
        this.bootstrap.setFactory(factory);
        this.bootstrap.setOption("child.tcpNoDelay", true);
        this.bootstrap.setOption("child.keepAlive", true);
        
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, false);
        this.bootstrap.setPipelineFactory(pipelineFactory);
        
        Channel acceptor = this.bootstrap.bind(new InetSocketAddress(this.portNumber));
        if (!acceptor.isBound()) {
            this.bootstrap.releaseExternalResources();
            System.out.println("Server failed to bind port: " + this.portNumber + " is there already a server running?");
            this.isAlive = false;
            this.errored = true;
            return;
        }
        
        System.out.println("Server bound to port: " + this.portNumber);
        this.channelGroup.add(acceptor);
        this.isAlive = true;
    }

    @Override
    public void run() {
        // TODO: Startup
        
        while (this.isAlive && !this.errored) {
            try {
                // TODO: Pulse threads incl session registry
                Thread.sleep(100);
            } 
            
            catch (InterruptedException ex) {
                // TODO: Fail notice
            }
        }
        
        if (this.errored) {
            this.main.setErrored("Error in server main thread", null);
            // TODO: Handle existing ex
        }
        
        this.stop();
    }
    
    public void stop() {
        this.channelGroup.close().awaitUninterruptibly();
        this.bootstrap.releaseExternalResources();
        System.out.println("Server stopped!");
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
        // TODO: handle saving
        this.isAlive = false;
    }

    @Override
    public void forceShutdown() {
        // TODO: handle force shutdown, work out what data can be trusted
        this.isAlive = false;
        this.errored = true;
    }
    
    public ChannelGroup getChannelGroup() {
        return this.channelGroup;
    }
    
    public SessionRegistry getSessionRegistry() {
        return this.sessionRegistry;
    }
}
