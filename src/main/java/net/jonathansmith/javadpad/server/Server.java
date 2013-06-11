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
package net.jonathansmith.javadpad.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import net.jonathansmith.javadpad.DPAD;
import net.jonathansmith.javadpad.DPAD.Platform;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;
import net.jonathansmith.javadpad.common.network.CommonPipelineFactory;
import net.jonathansmith.javadpad.common.util.filesystem.FileSystem;
import net.jonathansmith.javadpad.common.util.logging.DPADLoggerFactory;
import net.jonathansmith.javadpad.common.util.threads.NamedThreadFactory;
import net.jonathansmith.javadpad.common.util.threads.RuntimeThread;
import net.jonathansmith.javadpad.server.gui.ServerGUI;
import net.jonathansmith.javadpad.server.network.session.SessionRegistry;

/**
 *
 * @author jonathansmith
 */
public class Server extends Engine {
    
    private final ChannelGroup channelGroup;
    private final SessionRegistry sessionRegistry;
    
    private ServerBootstrap bootstrap;
    
    public Server(DPAD main, String host, int port) {
        super(main, Platform.SERVER, host, port);
        
        this.setGUI((TabbedGUI) new ServerGUI(this));
        this.setFileSystem(new FileSystem(this));
        
        // Netty setup
        this.channelGroup = new DefaultChannelGroup();
        this.sessionRegistry = new SessionRegistry(this);
        this.bootstrap = new ServerBootstrap();
    }
    
    public ChannelGroup getChannelGroup() {
        return this.channelGroup;
    }
    
    public SessionRegistry getSessionRegistry() {
        return this.sessionRegistry;
    }

    @Override
    public void init() {
        super.init();
        if (this.errored) {
            return;
        }
        
        this.setCompleteLogger(DPADLoggerFactory.getInstance().getLogger(this));
        
        // TODO: Config?
        // TODO: Connection pool
        // TODO: Default properties
        
        this.info("Beginning network initialisation");
        
        ChannelFactory factory;
        SocketAddress address;
        if (!this.hostName.contentEquals("local")) {
            ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Boss", true));
            ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Worker", true));
            factory = new NioServerSocketChannelFactory(boss, worker);
            address = new InetSocketAddress(this.portNumber);
        }
        
        else {
            factory = new DefaultLocalServerChannelFactory();
            address = new LocalAddress(this.portNumber);
        }
        
        this.bootstrap.setFactory(factory);
        this.bootstrap.setOption("child.tcpNoDelay", true);
        this.bootstrap.setOption("child.keepAlive", true);
        
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, false);
        this.bootstrap.setPipelineFactory(pipelineFactory);
        
        Channel acceptor = this.bootstrap.bind(address);
        if (!acceptor.isBound()) {
            this.bootstrap.releaseExternalResources();
            this.error("Server failed to bind port: " + this.portNumber + " is there already a server running?");
            this.isAlive = false;
            this.errored = true;
            return;
        }
        
        this.info("Server bound to port: " + this.portNumber);
        this.channelGroup.add(acceptor);
        this.isAlive = true;
    }

    @Override
    public void setRuntime(RuntimeThread thread) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    @Override
    public void sendQuitToRuntimeThread(String message, boolean error) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
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
                this.forceShutdown("Main thread interrupted", ex);
            }
        }
        
        if (this.errored) {
            this.main.setErrored("Error in server main thread", null);
        }
        
        this.stop();
    }
    
    public void stop() {
        this.channelGroup.close().awaitUninterruptibly();
        this.bootstrap.releaseExternalResources();
        System.out.println("Server stopped!");
    }

    @Override
    public void saveAndShutdown() {
        this.isAlive = false;
        
        // TODO: handle saving
        
        this.info("Shuttdown called on: " + this.platform.toString());
    }

    @Override
    public void forceShutdown(String cause, Throwable ex) {
        this.isAlive = false;
        this.errored = true;
        
        // TODO: handle force shutdown, work out what data can be trusted
        
        this.error(cause, ex);
    }
}
