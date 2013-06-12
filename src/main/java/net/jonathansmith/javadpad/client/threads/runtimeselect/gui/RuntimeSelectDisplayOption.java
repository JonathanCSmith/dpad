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
package net.jonathansmith.javadpad.client.threads.runtimeselect.gui;

import java.awt.event.ActionEvent;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.gui.DisplayOption;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.client.threads.ClientRuntimeThread;
import net.jonathansmith.javadpad.client.threads.runtimeselect.gui.panel.RuntimeSelectPane;
import net.jonathansmith.javadpad.client.threads.runtimeselect.gui.toolbar.RuntimeSelectToolbar;
import net.jonathansmith.javadpad.common.database.Batch;
import net.jonathansmith.javadpad.common.database.Experiment;
import net.jonathansmith.javadpad.common.database.User;

/**
 *
 * @author jonathansmith
 */
public class RuntimeSelectDisplayOption extends DisplayOption {

    public RuntimeSelectPane runtimeSelectPane;
    public RuntimeSelectToolbar runtimeSelectToolbar;
    
    public RuntimeSelectDisplayOption() {
        super();
        this.runtimeSelectPane = new RuntimeSelectPane();
        this.runtimeSelectToolbar = new RuntimeSelectToolbar();
        this.currentPanel = this.runtimeSelectPane;
        this.currentToolbar = this.runtimeSelectToolbar;
        
        this.runtimeSelectToolbar.setUser.addActionListener(this);
        this.runtimeSelectToolbar.setExperiment.addActionListener(this);
        this.runtimeSelectToolbar.setBatch.addActionListener(this);
    }
    
    @Override
    public void validateState() {
        Client client = (Client) this.engine;
        ClientSession session = client.getSession();
        
        if (session.getUser() == null) {
            this.runtimeSelectToolbar.setUser.setEnabled(true);
            this.runtimeSelectToolbar.setExperiment.setEnabled(false);
            this.runtimeSelectToolbar.setBatch.setEnabled(false);
        }
        
        else if (session.getExperiment() == null) {
            this.runtimeSelectToolbar.setUser.setEnabled(true);
            this.runtimeSelectToolbar.setExperiment.setEnabled(true);
            this.runtimeSelectToolbar.setBatch.setEnabled(false);
        }
        
        else if (session.getBatch() == null) {
            this.runtimeSelectToolbar.setUser.setEnabled(true);
            this.runtimeSelectToolbar.setExperiment.setEnabled(true);
            this.runtimeSelectToolbar.setBatch.setEnabled(true);
        }
        
        if (this.currentPanel == this.runtimeSelectPane) {
            User user = session.getUser();
            
            if (user != null) {
                this.runtimeSelectPane.jTextField1.setText(user.getUsername());
                
                Experiment experiment = session.getExperiment();
                
                if (experiment != null) {
                    this.runtimeSelectPane.jTextField2.setText(experiment.getName());
                    
                    Batch batch = session.getBatch();
                    
                    if (batch != null) {
                        this.runtimeSelectPane.jTextField3.setText(batch.getName());
                    }
                    
                    else {
                        this.runtimeSelectPane.jTextField3.setText("");
                    }
                }
                
                else {
                    this.runtimeSelectPane.jTextField2.setText("");
                    this.runtimeSelectPane.jTextField3.setText("");
                }
            }
            
            else {
                this.runtimeSelectPane.jTextField1.setText("");
                this.runtimeSelectPane.jTextField2.setText("");
                this.runtimeSelectPane.jTextField3.setText("");
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        Client client = (Client) this.engine;
        ClientSession session = client.getSession();
        
        if (evt.getSource() == this.runtimeSelectToolbar.setUser) {
            client.setRuntime(ClientRuntimeThread.USER);
        }
        
//        else if (evt.getSource() == this.runtimeSelectToolbar.setExperiment) {
//            client.setRuntime(ClientRuntimeThread.EXPERIMENT_SELECT);
//        }
//        
//        else if (evt.getSource() == this.runtimeSelectToolbar.setBatch) {
//            client.setRuntime(ClientRuntimeThread.BATCH_SELECT);
//        }
//        
//        else if (evt.getSource() == this.runtimeSelectToolbar.addData) {
//            client.setRuntime(ClientRuntimeThread.LOAD_AND_PROCESS);
//        }
    }
}
