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
package net.jonathansmith.javadpad.gui.common.startup;

import java.awt.event.ActionListener;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.common.startup.panel.StartupPane;
import net.jonathansmith.javadpad.gui.common.startup.toolbar.StartupToolbar;

/**
 *
 * @author jonathansmith
 */
public class StartupDisplayOption extends DisplayOption {
    
    public StartupPane startupPane;
    public StartupToolbar startupToolbar;
    
    public StartupDisplayOption() {
        super();
        this.startupPane = new StartupPane();
        this.startupToolbar = new StartupToolbar();
        this.currentPanel = this.startupPane;
        this.currentToolbar = this.startupToolbar;
    }

    @Override
    public void validateState(DPADController controlller) {}

    @Override
    public void addDisplayListener(ActionListener listener) {
        this.startupPane.localRuntime.addActionListener(listener);
        this.startupPane.hostRuntime.addActionListener(listener);
        this.startupPane.connectRuntime.addActionListener(listener);
    }
}
