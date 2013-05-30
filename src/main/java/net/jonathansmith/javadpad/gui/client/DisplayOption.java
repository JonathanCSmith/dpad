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
package net.jonathansmith.javadpad.gui.client;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.controller.DPADController;

/**
 *
 * @author jonathansmith
 */
public abstract class DisplayOption {
    
    public JPanel currentPanel;
    public JPanel currentToolbar;
    public DPADController controller;
    
    public DisplayOption() {}
    
    public void setController(DPADController controller) {
        this.controller = controller;
    }
    
    public JPanel getCurrentView() {
        return this.currentPanel;
    }
    
    public void setCurrentView(JPanel panel) {
        this.currentPanel = panel;
    }
    
    public JPanel getCurrentToolbar() {
        return this.currentToolbar;
    }
    
    public void setCurrentToolbar(JPanel panel) {
        this.currentToolbar = panel;
    }
    
    public abstract void validateState(DPADController controlller);
    
    public abstract void addDisplayListener(ActionListener listener);
}
