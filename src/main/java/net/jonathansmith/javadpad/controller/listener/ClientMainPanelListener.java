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
package net.jonathansmith.javadpad.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.engine.thread.DPADConnectEngine;
import net.jonathansmith.javadpad.engine.thread.DPADHostEngine;
import net.jonathansmith.javadpad.engine.thread.DPADLocalEngine;

/**
 *
 * @author Jon
 */
public class ClientMainPanelListener implements ActionListener {
    
    private DPADController parent;
    
    public ClientMainPanelListener(DPADController controller) {
        this.parent = controller;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.parent.getGui().localRuntime) {
            this.parent.setEngine(new DPADLocalEngine(this.parent.logger));
        }
        
        else if (evt.getSource() == this.parent.getGui().hostRuntime) {
            this.parent.setEngine(new DPADHostEngine(this.parent.logger));
        }
        
        else if (evt.getSource() == this.parent.getGui().connectRuntime) {
            this.parent.setEngine(new DPADConnectEngine(this.parent.logger));
        }
    }
}
