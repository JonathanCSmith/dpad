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
package net.jonathansmith.javadpad.client.gui.main;

import java.awt.event.ActionEvent;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.gui.DisplayOption;
import net.jonathansmith.javadpad.client.gui.main.panel.ClientMainPane;
import net.jonathansmith.javadpad.client.gui.main.toolbar.ClientMainToolbar;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.common.database.Batch;
import net.jonathansmith.javadpad.common.database.Experiment;
import net.jonathansmith.javadpad.common.database.User;
import net.jonathansmith.javadpad.common.util.threads.RuntimeThread;
import net.jonathansmith.javadpad.toberefactored.controller.DPADController;

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
        
        this.mainToolbar.setUser.addActionListener(this);
        this.mainToolbar.setExperiment.addActionListener(this);
        this.mainToolbar.setBatch.addActionListener(this);
    }
    
    @Override
    public void validateState(DPADController controlller) {
        Client client = (Client) this.engine;
        ClientSession session = client.getSession();
        
        if (session.getUser() == null) {
            this.mainToolbar.setUser.setEnabled(true);
            this.mainToolbar.setExperiment.setEnabled(false);
            this.mainToolbar.setBatch.setEnabled(false);
        }
        
        else if (session.getExperiment() == null) {
            this.mainToolbar.setUser.setEnabled(true);
            this.mainToolbar.setExperiment.setEnabled(true);
            this.mainToolbar.setBatch.setEnabled(false);
        }
        
        else if (session.getBatch() == null) {
            this.mainToolbar.setUser.setEnabled(true);
            this.mainToolbar.setExperiment.setEnabled(true);
            this.mainToolbar.setBatch.setEnabled(true);
        }
        
        if (this.currentPanel == this.mainPane) {
            User user = session.getUser();
            
            if (user != null) {
                this.mainPane.jTextField1.setText(user.getUsername());
                
                Experiment experiment = session.getExperiment();
                
                if (experiment != null) {
                    this.mainPane.jTextField2.setText(experiment.getName());
                    
                    Batch batch = session.getBatch();
                    
                    if (batch != null) {
                        this.mainPane.jTextField3.setText(batch.getName());
                    }
                    
                    else {
                        this.mainPane.jTextField3.setText("");
                    }
                }
                
                else {
                    this.mainPane.jTextField2.setText("");
                    this.mainPane.jTextField3.setText("");
                }
            }
            
            else {
                this.mainPane.jTextField1.setText("");
                this.mainPane.jTextField2.setText("");
                this.mainPane.jTextField3.setText("");
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        Client client = (Client) this.engine;
        ClientSession session = client.getSession();
        
        if (evt.getSource() == this.mainToolbar.setUser) {
            client.setRuntime(RuntimeThread.USER_SELECT);
        }
        
        else if (evt.getSource() == this.mainToolbar.setExperiment) {
            client.setRuntime(RuntimeThread.EXPERIMENT_SELECT);
        }
        
        else if (evt.getSource() == this.mainToolbar.setBatch) {
            client.setRuntime(RuntimeThread.BATCH_SELECT);
        }
        
        else if (evt.getSource() == this.mainToolbar.addData) {
            client.setRuntime(RuntimeThread.LOAD_AND_PROCESS);
        }
    }
}
