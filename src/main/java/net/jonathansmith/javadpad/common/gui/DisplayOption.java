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
package net.jonathansmith.javadpad.common.gui;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.api.guicontroller.IDisplayController;
import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.network.session.ClientSession;

/**
 *
 * @author jonathansmith
 */
public abstract class DisplayOption implements IDisplayController {
    
    public JPanel currentPanel;
    public JPanel currentToolbar;
    public Client engine;
    public ClientSession session;
    
    private boolean bound = false;
    
    public DisplayOption() {}
    
    @Override
    public void bind() {
        this.bound = true;
    }
    
    @Override
    public void unbind() {
        this.bound = false;
    }
    
    public boolean isBound() {
        return this.bound;
    }
    
    public void setEngine(Client engine, ClientSession session) {
        this.engine = engine;
        this.session = session;
    }
    
    @Override
    public JPanel getCurrentView() {
        return this.currentPanel;
    }
    
    @Override
    public void setCurrentView(JPanel panel) {
        this.currentPanel = panel;
    }
    
    @Override
    public JPanel getCurrentToolbar() {
        return this.currentToolbar;
    }
    
    @Override
    public void setCurrentToolbar(JPanel panel) {
        this.currentToolbar = panel;
    }
}