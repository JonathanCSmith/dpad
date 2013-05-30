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

import java.io.File;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.engine.connect.DPADConnectEngine;
import net.jonathansmith.javadpad.engine.host.DPADHostEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.engine.local.process.Startup_LocalProcess;
import net.jonathansmith.javadpad.gui.startup.StartupDisplayOption;
import net.jonathansmith.javadpad.util.RuntimeType;

/**
 *
 * @author Jon
 */
public class StartupPanelListener implements ActionListener {
    
    private DPADController parent;
    
    public StartupPanelListener(DPADController controller) {
        this.parent = controller;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        StartupDisplayOption display = (StartupDisplayOption) RuntimeType.RUNTIME_SELECT.getDisplay();
        if (evt.getSource() == display.startupPane.localRuntime) {
            String outcome = this.parent.getGui().getDirectory();
            if (outcome.equals("")) {
                return;    
            }
            
            File file = new File(outcome);
            if (!file.exists()) {
                return;
            }
            
            DPADLocalEngine local = new DPADLocalEngine(this.parent.fileSystem);
            this.parent.setEngine(local);
            ((Startup_LocalProcess) local.getRuntime()).setAttemptConnection(outcome);
        }
        
        else if (evt.getSource() == display.startupPane.hostRuntime) {
            String outcome = this.parent.getGui().getDirectory();
            if (outcome.equals("")) {
                return;    
            }
            
            File file = new File(outcome);
            if (!file.exists()) {
                return;
            }
            
            DPADHostEngine host = new DPADHostEngine(this.parent.fileSystem);
            this.parent.setEngine(host);
            //((DPADHostEngine) host.getRuntime()).createOrManageDatabase(outcome);
        }
        
        else if (evt.getSource() == display.startupPane.connectRuntime) {
            this.parent.setEngine(new DPADConnectEngine(this.parent.fileSystem));
        }
    }
}
