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
package net.jonathansmith.javadpad;

import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.gui.MainGUI;
import net.jonathansmith.javadpad.common.gui.StartupViewController;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;
import net.jonathansmith.javadpad.common.network.packet.DefaultPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyResponsePacket;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataPacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataUpdatePacket;
import net.jonathansmith.javadpad.common.network.packet.session.NewSessionDataPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginTransferPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginUploadRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.util.PlatformConverter;
import net.jonathansmith.javadpad.server.Server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 *
 * @author Jon
 */
public class DPAD extends Thread {
    
    public enum Platform {
        CLIENT,
        SERVER,
        LOCAL;
    }
    
    @Parameter(names = {"-platform"}, converter = PlatformConverter.class, description = "Runtime type")
    public Platform platform = null;
    
    @Parameter(names = {"-ip"}, description = "Address to host or connect")
    public String host = "127.0.0.1";
    
    @Parameter(names = {"-port"}, description = "Port to host or connect")
    public Integer port = 6889;
    
    @Parameter(names ={"-debug"}, description = "Verbosity of console logging")
    public boolean debug = true;
    
    private MainGUI gui;
    private List<Engine> runningEngines = new LinkedList<Engine> ();
    private boolean running;
    private boolean hasError;
    private String cause;
    private Throwable error;
    
    public DPAD() {}
    
    public Platform getRuntimeSelected() {
        return this.platform;
    }
    
    public void setRuntimeSelected(Platform platform) {
        this.platform = platform;
    }
    
    public void setURI(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void setErrored(String cause, Throwable ex) {
        this.hasError = true;
        this.cause = cause;
        this.error = ex;
    }
    
    public void acquireTab(Platform platform, TabbedGUI panel) {
        this.gui.addTab(platform.toString().toLowerCase(), panel);
    }
    
    public void removeTab(TabbedGUI panel) {
        this.gui.removeTab(panel);
    }
    
    public void init(MainGUI gui) {
        this.gui = gui;
        this.addPackets();
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        
        if (this.platform == Platform.SERVER || this.platform == Platform.LOCAL) {
            this.runningEngines.add(new Server(this, this.host, this.port));
        }
        
        if (this.platform == Platform.CLIENT || this.platform == Platform.LOCAL) {
            this.runningEngines.add(new Client(this, this.host, this.port));
        }
        
        for (Engine engine : this.runningEngines) {
            engine.init();
        }
    }
    
    private void addPackets() {
        Packet.addPacket(DefaultPacket.class);
        Packet.addPacket(HandshakePacket.class);
        Packet.addPacket(EncryptionKeyRequestPacket.class);
        Packet.addPacket(EncryptionKeyResponsePacket.class);
        Packet.addPacket(EncryptedSessionKeyPacket.class);
        Packet.addPacket(DataPacket.class);
        Packet.addPacket(DataRequestPacket.class);
        Packet.addPacket(DataUpdatePacket.class);
        Packet.addPacket(NewSessionDataPacket.class);
        Packet.addPacket(SetSessionDataPacket.class);
        Packet.addPacket(PluginUploadRequestPacket.class);
        Packet.addPacket(PluginTransferPacket.class);
    }
    
    @Override
    public void run() {
        this.running = true;
        this.hasError = false;
        
        for (Engine engine : this.runningEngines) {
            engine.start();
        }
        
        while (!this.hasError && this.running) {
            try {
                boolean runnings = false;
                for (Engine engine : this.runningEngines) {
                    runnings |= engine.isRunning();
                }
                
                if (!runnings) {
                    this.running = false;
                }
                
                Thread.sleep(100);
                
            } catch (InterruptedException ex) {
                for (Engine engine : this.runningEngines) {
                    engine.error("Background thread interrupted, critical failure", ex);
                }
                this.hasError = true;
                this.running = false;
            }
        }
        
        if (this.hasError) {
            for (Engine engine : this.runningEngines) {
                engine.error("An error has forced all threads to shutdown");
                engine.error("Data integrity cannot currently be guaranteed");
                engine.error("===============================================");
                engine.error(cause, this.error);
                engine.error("===============================================");
            }
        }
        
        for (Engine engine : this.runningEngines) {
            if (engine.isViable() && engine.isRunning()) {
                engine.saveAndShutdown();
            }
            
            else {
                if (engine.isRunning()) {
                    engine.forceShutdown("Shutdown by main", null);
                }
            }
        }
    }
    
    public void shutdown() {
        this.running = false;
    }
    
    public static void main(String[] args) {
        DPAD dpad = new DPAD();
        MainGUI gui = new MainGUI(dpad);
        JCommander commands = new JCommander(dpad);
        commands.parse(args);
        
        if (dpad.getRuntimeSelected() == null) {
            gui.init();
            StartupViewController controller = new StartupViewController(gui, dpad);
            gui.run();
            
            while (dpad.getRuntimeSelected() == null && gui.isVisible() && gui.isShowing()) {
                try {
                    Thread.sleep(100);
                }
                
                catch (InterruptedException ex) {
                    System.out.println("Failure to launch!");
                    ex.printStackTrace();
                    Runtime.getRuntime().halt(1);
                    return;
                }
            }
            
            gui.disposeOfStartupPane();
        }
        
        dpad.init(gui);
        dpad.start();
        
        try {
            dpad.join();
        }
        
        catch (InterruptedException ex) {
            System.out.println("Failure to launch!");
            ex.printStackTrace();
        }
        
        if (dpad.hasError) {
            Runtime.getRuntime().halt(1);
        }
        
        else {
            Runtime.getRuntime().halt(0);
        }
    }
}
