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
package net.jonathansmith.javadpad.client.threads.data.overview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.common.gui.DisplayOption;
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.pane.CurrentRecordPane;
import net.jonathansmith.javadpad.client.threads.ClientRuntimeThread;
import net.jonathansmith.javadpad.client.threads.data.overview.pane.DataPane;
import net.jonathansmith.javadpad.client.threads.data.overview.toolbar.DataToolbar;
import net.jonathansmith.javadpad.api.database.Record;
import net.jonathansmith.javadpad.api.database.records.Experiment;
import net.jonathansmith.javadpad.api.events.Event;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionFocusPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class DataDisplayOption extends DisplayOption implements ActionListener, EventListener {

    // Main
    public CurrentRecordPane currentInformationPane;
    public DataToolbar toolbar;
    
    private WaitForRecordsDialog dialog;
    
    public DataDisplayOption() {
        super();
        this.currentInformationPane = new DataPane();
        this.toolbar = new DataToolbar();
        
        this.addListeners();
        
        this.currentPanel = this.currentInformationPane;
        this.currentToolbar = this.toolbar;
    }
    
    private void addListeners() {
        this.toolbar.addDisplayOptionListener(this);
    }

    @Override
    public void bind() {
        super.bind();
        this.engine.getEventThread().addListener(DataArriveEvent.class, this);
    }

    @Override
    public void unbind() {
        this.engine.getEventThread().removeListener(this);
        super.unbind();
    }
    
    @Override
    public void validateState() {
        SessionData focus = this.session.getSessionFocusType();
        if (focus != SessionData.EXPERIMENT) {
            LockedPacket p = new SetSessionFocusPacket(this.engine, this.session, SessionData.EXPERIMENT);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
            
            RecordsList<Record> exper = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.EXPERIMENT);
            if (exper == null || exper.isEmpty()) {
                this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setVisible(true);
                    }
                });
            }
            
            else {
                this.currentInformationPane.setCurrentData((Experiment) exper.getFirst());
                this.handleButtonStates((Experiment) exper.getFirst());
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.toolbar.addData) {
            this.engine.forceSetRuntime(ClientRuntimeThread.ADD_DATA, true);
        }
        
        // Main transition to previous
        else if (evt.getSource() == this.toolbar.back) {
            this.returnToPreviousFromMain();
        }
    }

    // Listen for session events etc, forward to plugin
    public void changeEventReceived(Event evt) {
        if (evt instanceof DataArriveEvent) {
            DataArriveEvent event = (DataArriveEvent) evt;
            SessionData arrivedType = (SessionData) event.getSource();
            
            if (arrivedType == SessionData.EXPERIMENT) {
                RecordsList<Record> result = this.session.softlyCheckoutSessionData(arrivedType);
                if (result == null || result.isEmpty() || !(result.getFirst() instanceof Experiment)) {
                    return;
                }
                
                Experiment record = (Experiment) result.getFirst();
                this.currentInformationPane.setCurrentData(record);
                
                this.handleButtonStates(record);
                
                if (this.dialog != null) {
                    this.dialog.maskCloseEvent();
                    this.dialog.dispose();
                    this.dialog = null;
                }
            }
        }
    }
    
    private void handleButtonStates(Experiment e) {
        // TODO: Finish this
    }
    
    private void returnToPreviousFromMain() {
        if (this.currentPanel == this.currentInformationPane) {
            LockedPacket p = new SetSessionFocusPacket(this.engine, this.session, SessionData.USER);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);

            this.engine.returnToDefaultRuntime("User called back", false);
        }
    }
}