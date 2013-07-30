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

import org.apache.log4j.Level;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import net.jonathansmith.javadpad.DPAD;
import net.jonathansmith.javadpad.api.Platform;
import net.jonathansmith.javadpad.api.events.Event;
import net.jonathansmith.javadpad.api.threads.IRuntime;
import net.jonathansmith.javadpad.api.threads.IThread;
import net.jonathansmith.javadpad.client.gui.ClientGUI;
import net.jonathansmith.javadpad.client.network.listeners.ClientConnectListener;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.client.threads.ClientRuntimeThread;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.thread.ThreadChangeEvent;
import net.jonathansmith.javadpad.common.events.thread.ThreadShutdownEvent;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.session.DisconnectPacket;
import net.jonathansmith.javadpad.common.network.protocol.CommonPipelineFactory;
import net.jonathansmith.javadpad.common.network.session.Session.NetworkThreadState;
import net.jonathansmith.javadpad.common.util.filesystem.FileSystem;
import net.jonathansmith.javadpad.common.util.logging.DPADLoggerFactory;
import net.jonathansmith.javadpad.common.util.threads.NamedThreadFactory;

/**
 *
 * @author jonathansmith
 */
public class Client extends Engine implements EventListener {
    
    private ClientBootstrap bootstrap;
    private ClientSession session;
    private IRuntime currentThread;
    private boolean disconnectExpected = false;
    
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
    
    public IRuntime getRuntime() {
        return this.currentThread;
    }

    private void setRuntime(IRuntime thread) {
        if (this.currentThread != null) {
            if (this.currentThread.isRunnable() && this.currentThread.getThread().isRunning()) {
                return;
            }
            
            this.currentThread.getDisplay().unbind();
            thread.getDisplay().bind();
        }
        
        ThreadChangeEvent evt = new ThreadChangeEvent(thread);
        this.currentThread = thread;
        this.fireChange(evt);
    }
    
    public void forceSetRuntime(IRuntime thread, boolean force) {
        if (this.currentThread != null) {
            if (this.currentThread.isRunnable() && this.currentThread.getThread().isRunning()) {
                if (force) {
                    this.currentThread.getThread().shutdown(force);
                }
                
                else {
                    return;
                }
            }
        }
        
        this.setRuntime(thread);
    }

    public void returnToDefaultRuntime(String message, boolean error) {
        this.forceSetRuntime(ClientRuntimeThread.RUNTIME_SELECT, error);
    }
    
    public void fireChange(Event evt) {
        this.getEventThread().post(evt);
    }

    public void changeEventReceived(Event event) {
        if (event instanceof ThreadShutdownEvent && event.getSource() == this.currentThread) {
            this.setRuntime(ClientRuntimeThread.RUNTIME_SELECT);
        }
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
        this.setRuntime(ClientRuntimeThread.STARTUP);
        
        // TODO: Config?
        // TODO: Default properties
        
        this.info("Beginning network initialisation");
        
        // Add our appenders to existing loggers
        DPADLoggerFactory.getInstance().getLogger(this, "org.jboss.netty", Level.INFO);
        
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
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this);
        this.bootstrap.setPipelineFactory(pipelineFactory);
        
        ChannelFuture future = this.bootstrap.connect(address);
        future.addListener((ChannelFutureListener) new ClientConnectListener(this));
        if (!future.awaitUninterruptibly().isSuccess()) {
            this.error("Client failed to connect to server: " + this.hostName + ": " + this.portNumber);
            this.bootstrap.releaseExternalResources();
            this.isAlive = false;
            this.errored = true;
            return;
        }
        
        this.getEventThread().addListener(ThreadShutdownEvent.class, this);
        this.debug("Client connected to server: " + this.hostName + ": " + this.portNumber);
        this.isAlive = true;
    }

    @Override
    public void run() {
        while (this.isAlive && !this.errored) {
            try {
                // 1) Await authentication and handle disconnect
                if (this.session == null || this.session.getState() != NetworkThreadState.RUNNING) {
                    Thread.sleep(100);
                    continue;
                }
                
                // 1) Start runtime selection thread
                if (this.getRuntime() == ClientRuntimeThread.STARTUP) {
                    this.setRuntime(ClientRuntimeThread.RUNTIME_SELECT);
                }
                
                IRuntime currentRuntime = this.getRuntime();
                if (currentRuntime.isRunnable()) {
                    IThread thread = currentRuntime.getThread();
                    thread.init(this.getEventThread());
                    thread.start();
                    thread.join();
                } 
                
                else {
                    Thread.sleep(100);
                }
            }
            
            catch (InterruptedException ex) {
                this.forceShutdown("Main thread interrupted", ex);
            }
        }
        
//        if (this.errored) {
//            this.main.setErrored("Error in client main thread", null);
//        }
        
        // No leaks
        this.finish();
    }
    
    public void finish() {
        if (this.session.channel != null) {
            this.session.channel.close().awaitUninterruptibly();
        }
        
        this.bootstrap.releaseExternalResources();
        this.info("Client Stopped!");
        
        // Allow main runtime preservation if possible
        this.main.removeTab(this.gui);
    }
    
    public void channelDisconnect() {
        if (!this.disconnectExpected) {
            this.forceShutdown("Server disconnected unexpectedly", null);
            return;
        }
        
        // TODO: Reason post?
        this.saveAndShutdown();
    }
    
    public void setDisconnectExpected() {
        this.disconnectExpected = true;
    }

    @Override
    public void saveAndShutdown() {
        this.getEventThread().shutdown(false);
        this.getPluginManager().shutdown(false);
        
        if (!this.disconnectExpected) {
            Packet p = new DisconnectPacket(this, this.session, false);
            this.session.addPacketToSend(PacketPriority.CRITICAL, p);
        }
        this.session.disconnect(false);
        
        this.returnToDefaultRuntime("Shutdown", false);
        
        this.info("Shutdown called on: " + this.platform.toString());
        this.isAlive = false;
    }

    @Override
    public void forceShutdown(String message, Throwable ex) {
        this.getEventThread().shutdown(true);
        this.getPluginManager().shutdown(true);
        this.session.disconnect(true);
        
        this.returnToDefaultRuntime(message, true);
        
        this.error(message, ex);
        this.isAlive = false;
        this.errored = true;
    }
}