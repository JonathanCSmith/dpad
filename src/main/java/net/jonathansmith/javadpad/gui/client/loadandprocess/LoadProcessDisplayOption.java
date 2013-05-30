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
package net.jonathansmith.javadpad.gui.client.loadandprocess;

import java.awt.event.ActionEvent;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.loadandprocess.panel.LPMainPanel;
import net.jonathansmith.javadpad.gui.client.loadandprocess.toolbar.LPMainToolbar;

/**
 *
 * @author Jon
 */
public class LoadProcessDisplayOption extends DisplayOption {

    public LPMainPanel mainPane;
    public LPMainToolbar mainToolbar;
    
    public LoadProcessDisplayOption() {
        super();
        this.mainPane = new LPMainPanel();
        this.mainToolbar = new LPMainToolbar();
        this.currentPanel = this.mainPane;
        this.currentToolbar = this.mainToolbar;
    }
    
    @Override
    public void validateState(DPADController controlller) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
