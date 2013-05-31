/*
 * Copyright (C) 2013 Jonathan Smith
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

import net.jonathansmith.javadpad.aaaarewrite.client.ClientMainThread;
import net.jonathansmith.javadpad.aaaarewrite.server.ServerMainThread;
import net.jonathansmith.javadpad.aaaarewrite.startup.StartupGUI;
import net.jonathansmith.javadpad.aaaarewrite.startup.StartupViewController;
import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author Jonathan Smith
 */
public class DPAD extends Thread {
    
    public static boolean runtimeType = true;
    
    private boolean running = false;
    private boolean errored = false;
    
    private int runtime = -1;
    private String host;
    private int port;
    
    private ClientMainThread client = null;
    private ServerMainThread server = null;
    
    public void setRuntimeSelected(int runtime) {
        if (runtime >= 3 || runtime < 0) {
            return;
        }
        
        this.runtime = runtime;
    }
    
    public int getRuntimeSelected() {
        return this.runtime;
    }
    
    public void setURI(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void init() {
        switch (this.runtime) {
            case 0:
                this.client = new ClientMainThread("localhost", 6889);
                this.client.init();
                
                this.server = new ServerMainThread("localhost", 6889);
                this.server.init();
                
                return;
                
            case 1:
                this.server = new ServerMainThread(this.host, this.port);
                this.server.init();
                
                return;
                
            case 2:
                this.client = new ClientMainThread(this.host, this.port);
                
                return;
                
            default:
                this.errored = true;
                return;
        }
    }
    
    @Override
    public void run() {
        this.running = true;
        
        switch (this.runtime) {
            case 0:
                this.client.run();
                this.server.run();
                break;
                
            case 1:
                this.server.run();
                break;
                
            case 2:
                this.client.run();
                break;
                
            default:
                this.errored = true;
        }
        
        while (!this.errored && this.running) {
            try {
                Thread.sleep(100);
                
            } catch (InterruptedException ex) {
                System.out.println("Background thread interrupted, critical failure");
                ex.printStackTrace();
                this.errored = true;
                this.running = false;
            }
        }
        
        if (this.errored) {
            System.out.println("An error has forced all threads to shutdown");
            System.out.println("Data integrity cannot currently be guaranteed");
            System.out.println("Attempting to salvage");
        }
        
        switch (this.runtime) {
            case 0:
                if (this.client.isViable()) {
                    this.client.saveAndShutdown();
                }
                
                if (this.server.isViable()) {
                    this.server.saveAndShutdown();
                }
                
                break;
                
            case 1:
                if (this.server.isViable()) {
                    this.server.saveAndShutdown();
                }
                
                break;
                
            case 2:
                if (this.client.isViable()) {
                    this.client.saveAndShutdown();
                }
                
                break;
                
            default:
                
        }
    }
    
    /**
     * @param args the command line arguments
     */
    @SuppressWarnings({"CallToThreadDumpStack", "SleepWhileInLoop"})
    public static void main(String[] args) {
        if (runtimeType) {
            DPAD dpad = new DPAD();
            StartupGUI gui = new StartupGUI();
            gui.init();
            StartupViewController controller = new StartupViewController(gui, dpad);
            gui.run();
            
            while (dpad.getRuntimeSelected() == -1) {
                try {
                    Thread.sleep(100);
                    
                } catch (InterruptedException ex) {
                    System.out.println("Failure to launch!");
                    ex.printStackTrace();
                    return;
                }
            }
            
            dpad.init();
            dpad.run();
            
            try {
                dpad.join();
                
            } catch (InterruptedException ex) {
                System.out.println("Failure to launch!");
                ex.printStackTrace();
            }
            
            Runtime.getRuntime().halt(1);
        }
        
        else {
            DPADController controller = new DPADController();
            controller.init();

            if (!controller.errored && controller.initialised) {
                controller.start();

                try {
                    controller.join();
                } catch (InterruptedException ex) {
                    DPADLogger.severe("Runtime interruption, DPAD shutting down");
                }

                if (controller.errored) {
                    DPADLogger.severe("Runtime failure, DPAD shutting down");
                }

            } else {
                DPADLogger.severe("Failed to setup runtime environment");
            }

            Runtime.getRuntime().halt(1);
        }
    }
}
