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
package net.jonathansmith.javadpad.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import net.jonathansmith.javadpad.DPAD;
import net.jonathansmith.javadpad.DPAD.Platform;
import net.jonathansmith.javadpad.client.gui.ClientGUI;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;
import net.jonathansmith.javadpad.common.network.CommonPipelineFactory;
import net.jonathansmith.javadpad.common.util.filesystem.FileSystem;
import net.jonathansmith.javadpad.common.util.logging.DPADLoggerFactory;
import net.jonathansmith.javadpad.common.util.threads.NamedThreadFactory;
import net.jonathansmith.javadpad.common.util.threads.RuntimeThread;

/**
 *
 * @author jonathansmith
 */
public class Client extends Engine {
    
    private ClientBootstrap bootstrap;
    private ClientSession session;
    
    public Client(DPAD main, String host, int port) {
        super(main, Platform.CLIENT, host, port);
        
        this.setGUI((TabbedGUI) new ClientGUI(this));
        this.setFileSystem(new FileSystem(this));
        
        // Netty setup
        this.bootstrap = new ClientBootstrap();
    }
    
    public ClientSession getSession() {
        return this.session;
    }
    
    public void setSession(ClientSession sess) {
        this.session = sess;
    }
    
    @Override
    public ClientGUI getGUI() {
        return (ClientGUI) super.getGUI();
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
            ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Client - Boss", true));
            ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Client - Worker", true));
            factory = new NioClientSocketChannelFactory(boss, worker);
            address = new InetSocketAddress(this.hostName, this.portNumber);
        }
        
        else {
            factory = new DefaultLocalClientChannelFactory();
            address = new LocalAddress(this.portNumber);
        }
        
        this.bootstrap.setFactory(factory);
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, true);
        this.bootstrap.setPipelineFactory(pipelineFactory);
        
        ChannelFuture future = this.bootstrap.connect(address);
        if (!future.awaitUninterruptibly().isSuccess()) {
            this.error("Client failed to connect to server: " + this.hostName + ": " + this.portNumber);
            this.bootstrap.releaseExternalResources();
            this.isAlive = false;
            this.errored = true;
            return;
        }
        
        this.debug("Client connected to server: " + this.hostName + ": " + this.portNumber);
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
                // TODO: Pulse client threads
                Thread.sleep(100);
            }
            
            catch (InterruptedException ex) {
                this.forceShutdown("Main thread interrupted", ex);
            }
        }
        
        if (this.errored) {
            this.main.setErrored("Error in client main thread", null);
        }
        
        this.stop();
    }
    
    public void stop() {
        if (this.session.channel != null) {
            this.session.channel.close().awaitUninterruptibly();
        }
        
        this.bootstrap.releaseExternalResources();
        System.out.println("Client Stopped!");
    }

    @Override
    public void saveAndShutdown() {
        this.isAlive = false;
        
        // TODO: handle saving
        
        this.info("Shutdown called on: " + this.platform.toString());
    }

    @Override
    public void forceShutdown(String message, Throwable ex) {
        this.isAlive = false;
        this.errored = true;
        
        // TODO: handle force shutdown, work out what data can be trusted
        
        this.error(message, ex);
    }
}
