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

import java.net.InetSocketAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import net.jonathansmith.javadpad.aaaarewrite.DPADNew;
import net.jonathansmith.javadpad.aaaarewrite.common.network.CommonPipelineFactory;
import net.jonathansmith.javadpad.aaaarewrite.common.network.session.Session;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.NamedThreadFactory;

/**
 *
 * @author jonathansmith
 */
public class Client extends Engine {
    
    private ClientBootstrap bootstrap;
    private Channel channel;
    private Session session;
    
    public Client(DPADNew main, String host, int port) {
        super(main, host, port);
        this.bootstrap = new ClientBootstrap();
        
        // TODO: FileSystem
    }
    
    public void setSession(Session sess) {
        this.session = sess;
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
            ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Client - Boss", true));
            ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Client - Worker", true));
            factory = new NioClientSocketChannelFactory(boss, worker);
        }
        
        else {
            factory = new DefaultLocalClientChannelFactory();
        }
        
        this.bootstrap.setFactory(factory);
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, true);
        this.bootstrap.setPipelineFactory(pipelineFactory);
        
        ChannelFuture future = this.bootstrap.connect(new InetSocketAddress(this.hostName, this.portNumber));
        if (!future.awaitUninterruptibly().isSuccess()) {
            System.out.println("Client failed to connect to server: " + this.hostName + ": " + this.portNumber);
            this.bootstrap.releaseExternalResources();
            this.isAlive = false;
            this.errored = true;
            return;
        }
        
        System.out.println("Client connected to server: " + this.hostName + ": " + this.portNumber);
        this.channel = future.getChannel();
        this.isAlive = true;
    }

    @Override
    public void run() {
        // TODO: Startup
        
        while (this.isAlive && !this.errored) {
            try {
                // TODO: Pulse client threads
                Thread.sleep(100);
            }
            
            catch (InterruptedException ex) {
                // TODO: Fail Notice
            }
        }
        
        if (this.errored) {
            this.main.setErrored("Error in client main thread", null);
            // TODO: Handle existing ex
        }
        
        this.stop();
    }
    
    public void stop() {
        if (this.channel != null) {
            this.channel.close().awaitUninterruptibly();
        }
        
        this.bootstrap.releaseExternalResources();
        System.out.println("Client Stopped!");
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
}
