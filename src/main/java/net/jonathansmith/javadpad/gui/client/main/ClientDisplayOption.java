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
package net.jonathansmith.javadpad.gui.client.main;

import java.awt.event.ActionListener;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.main.panel.ClientMainPane;
import net.jonathansmith.javadpad.gui.client.main.toolbar.ClientMainToolbar;

/**
 *
 * @author jonathansmith
 */
public class ClientDisplayOption extends DisplayOption {

    public ClientMainPane mainPane;
    public ClientMainToolbar mainToolbar;
    
    public ClientDisplayOption() {
        super();
        this.mainPane = new ClientMainPane();
        this.mainToolbar = new ClientMainToolbar();
        this.currentPanel = this.mainPane;
        this.currentToolbar = this.mainToolbar;
    }
    
    @Override
    public void validateState(DPADController controlller) {
        if (this.controller.getSessionUser() == null) {
            this.mainToolbar.setUser.setEnabled(true);
            this.mainToolbar.setExperiment.setEnabled(false);
            this.mainToolbar.setBatch.setEnabled(false);
        }
        
        else if (this.controller.getSessionExperiment() == null) {
            this.mainToolbar.setUser.setEnabled(true);
            this.mainToolbar.setExperiment.setEnabled(true);
            this.mainToolbar.setBatch.setEnabled(false);
        }
        
        else if (this.controller.getSessionBatch() == null) {
            this.mainToolbar.setUser.setEnabled(true);
            this.mainToolbar.setExperiment.setEnabled(true);
            this.mainToolbar.setBatch.setEnabled(true);
        }
    }

    @Override
    public void addDisplayListener(ActionListener listener) {
        this.mainToolbar.setUser.addActionListener(listener);
        this.mainToolbar.setExperiment.addActionListener(listener);
        this.mainToolbar.setBatch.addActionListener(listener);
    }
}
