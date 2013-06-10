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
package net.jonathansmith.javadpad.aaaarewrite.common.startup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URI;
import java.net.URISyntaxException;

import net.jonathansmith.javadpad.aaaarewrite.DPADNew;
import net.jonathansmith.javadpad.aaaarewrite.DPADNew.Platform;

/**
 *
 * @author jonathansmith
 */
public class StartupViewController implements ActionListener {
    
    private DPADNew main;
    private StartupGUI gui;
    
    public StartupViewController(StartupGUI gui, DPADNew dpad) {
        this.main = dpad;
        this.gui = gui;
        this.addListeners();
    }
    
    private void addListeners() {
        this.gui.localRuntime.addActionListener(this);
        this.gui.hostRuntime.addActionListener(this);
        this.gui.connectRuntime.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.gui.localRuntime) {
            this.main.setRuntimeSelected(Platform.LOCAL);
            this.main.setURI("local", 6568);
        }
        
        else if (ae.getSource() == this.gui.hostRuntime) {
            String url = this.gui.databaseHostURL.getText();
            int port = Integer.parseInt(url);
            
            if (port < 0 || port > 65536) {
                this.main.setErrored("Invalid port number", new URISyntaxException(url, "Port Invalid"));
                this.main.interrupt();
                return;
            }
            
            this.main.setRuntimeSelected(Platform.SERVER);
            this.main.setURI("127.0.0.1", port);
        }
        
        else if (ae.getSource() == this.gui.connectRuntime) {
            String url = this.gui.databaseClientURL.getText();
            String host;
            int port;
            
            try {
                URI uri = new URI("my://" + url);
                host = uri.getHost();
                port = uri.getPort();
                
                if (host == null || port == -1) {
                    throw new URISyntaxException(uri.toString(), "URI invalid");
                }
                
            } catch (URISyntaxException ex) {
                this.main.interrupt();
                return;
            }
            
            this.main.setRuntimeSelected(Platform.CLIENT);
            this.main.setURI(host, port);
        }
    }
}
