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

import java.awt.event.ActionEvent;

import java.io.File;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.engine.connect.DPADConnectEngine;
import net.jonathansmith.javadpad.engine.host.DPADHostEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.engine.local.process.Startup_LocalProcess;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.common.startup.panel.StartupPane;
import net.jonathansmith.javadpad.gui.common.startup.toolbar.StartupToolbar;
import net.jonathansmith.javadpad.util.RuntimeType;

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
        
        this.startupPane.localRuntime.addActionListener(this);
        this.startupPane.hostRuntime.addActionListener(this);
        this.startupPane.connectRuntime.addActionListener(this);
    }

    @Override
    public void validateState(DPADController controlller) {}

    public void actionPerformed(ActionEvent evt) {
        StartupDisplayOption display = (StartupDisplayOption) RuntimeType.RUNTIME_SELECT.getDisplay();
        if (evt.getSource() == display.startupPane.localRuntime) {
            String outcome = this.controller.getGui().getDirectory();
            if (outcome.equals("")) {
                return;    
            }
            
            File file = new File(outcome);
            if (!file.exists()) {
                return;
            }
            
            DPADLocalEngine local = new DPADLocalEngine(this.controller.fileSystem);
            this.controller.setEngine(local);
            ((Startup_LocalProcess) local.getRuntime()).setAttemptConnection(outcome);
        }
        
        else if (evt.getSource() == display.startupPane.hostRuntime) {
            String outcome = this.controller.getGui().getDirectory();
            if (outcome.equals("")) {
                return;    
            }
            
            File file = new File(outcome);
            if (!file.exists()) {
                return;
            }
            
            DPADHostEngine host = new DPADHostEngine(this.controller.fileSystem);
            this.controller.setEngine(host);
            //((DPADHostEngine) host.getRuntime()).createOrManageDatabase(outcome);
        }
        
        else if (evt.getSource() == display.startupPane.connectRuntime) {
            this.controller.setEngine(new DPADConnectEngine(this.controller.fileSystem));
        }
    }
}
