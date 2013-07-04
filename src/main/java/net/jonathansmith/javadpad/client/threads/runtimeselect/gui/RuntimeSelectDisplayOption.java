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
import java.awt.event.ActionListener;

import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.threads.ClientRuntimeThread;
import net.jonathansmith.javadpad.client.threads.runtimeselect.gui.pane.RuntimeSelectPane;
import net.jonathansmith.javadpad.client.threads.runtimeselect.gui.toolbar.RuntimeSelectToolbar;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.records.Experiment;
import net.jonathansmith.javadpad.common.database.records.User;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionFocusPacket;
import net.jonathansmith.javadpad.common.network.session.Session.NetworkThreadState;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author jonathansmith
 */
public class RuntimeSelectDisplayOption extends DisplayOption implements ActionListener {

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
        this.runtimeSelectToolbar.setData.addActionListener(this);
        this.runtimeSelectToolbar.addPlugin.addActionListener(this);
        this.runtimeSelectToolbar.shutdown.addActionListener(this);
    }
    
    @Override
    public void validateState() {
        User user;
        RecordsList<Record> list = this.session.softlyCheckoutSessionData(SessionData.USER);
        if (list == null || list.isEmpty() || !(list.getFirst() instanceof User)) {
            user = null;
        }
        
        else {
            user = (User) list.getFirst();
        }
        
        Experiment experiment;
        list = this.session.softlyCheckoutSessionData(SessionData.EXPERIMENT);
        if (list == null || list.isEmpty() || !(list.getFirst() instanceof Experiment)) {
            experiment = null;
        }
        
        else {
            experiment = (Experiment) list.getFirst();
        }
        
        
        if (this.session.getState() != NetworkThreadState.RUNNING) {
            this.runtimeSelectToolbar.setUser.setEnabled(false);
            this.runtimeSelectToolbar.setExperiment.setEnabled(false);
            this.runtimeSelectToolbar.setData.setEnabled(false);
            return;
        }
        
        if (user == null) {
            this.runtimeSelectToolbar.setUser.setEnabled(true);
            this.runtimeSelectToolbar.setExperiment.setEnabled(false);
            this.runtimeSelectToolbar.setData.setEnabled(false);
        }
        
        else if (experiment == null) {
            this.runtimeSelectToolbar.setUser.setEnabled(true);
            this.runtimeSelectToolbar.setExperiment.setEnabled(true);
            this.runtimeSelectToolbar.setData.setEnabled(false);
            
            LockedPacket p = new SetSessionFocusPacket(this.engine, this.session, SessionData.USER);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
        }
        
        else {
            this.runtimeSelectToolbar.setUser.setEnabled(true);
            this.runtimeSelectToolbar.setExperiment.setEnabled(true);
            this.runtimeSelectToolbar.setData.setEnabled(true);
            
            LockedPacket p = new SetSessionFocusPacket(this.engine, this.session, SessionData.EXPERIMENT);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
        }
        
        if (this.currentPanel == this.runtimeSelectPane) {
            if (user != null) {
                this.runtimeSelectPane.usernameField.setText(user.getUsername());
                if (experiment != null) {
                    this.runtimeSelectPane.experimentField.setText(experiment.getName());
                }
            }
            
            else {
                this.runtimeSelectPane.usernameField.setText("");
                this.runtimeSelectPane.experimentField.setText("");
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.runtimeSelectToolbar.setUser) {
            this.engine.setRuntime(ClientRuntimeThread.USER);
        }
        
        else if (evt.getSource() == this.runtimeSelectToolbar.setExperiment) {
            this.engine.setRuntime(ClientRuntimeThread.EXPERIMENT);
        }
        
        else if (evt.getSource() == this.runtimeSelectToolbar.setData) {
            this.engine.setRuntime(ClientRuntimeThread.DATA);
        }
        
        else if (evt.getSource() == this.runtimeSelectToolbar.addPlugin) {
            this.engine.setRuntime(ClientRuntimeThread.ADD_PLUGIN);
        }
        
        else if (evt.getSource() == this.runtimeSelectToolbar.shutdown) {
            this.engine.saveAndShutdown();
        }
    }
}