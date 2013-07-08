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
package net.jonathansmith.javadpad.client.gui.displayoptions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.EventObject;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.CurrentRecordPane;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.ExistingRecordPane;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.NewRecordPane;
import net.jonathansmith.javadpad.client.gui.displayoptions.toolbar.RecordToolbar;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.session.NewSessionDataPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.database.recordaccess.QueryType;

/**
 *
 * @author Jon
 */
public class RecordsDisplayOption extends DisplayOption implements ActionListener, MouseListener, EventListener {
    
    public final DatabaseRecord recordType;
    public final CurrentRecordPane currentRecordPane;
    public final NewRecordPane newRecordPane;
    public final ExistingRecordPane existingRecordPane;
    public final RecordToolbar toolbar;
    
    private WaitForRecordsDialog dialog = null;
    
    public RecordsDisplayOption(DatabaseRecord type, CurrentRecordPane curr, NewRecordPane n, ExistingRecordPane exist, String title) {
        this.recordType = type;
        this.currentRecordPane = curr;
        this.newRecordPane = n;
        this.existingRecordPane = exist;
        this.toolbar = new RecordToolbar(title);
        
        this.addListeners();
        
        this.currentPanel = this.currentRecordPane;
        this.currentToolbar = this.toolbar;
    }
    
    private void addListeners() {
        this.newRecordPane.addDisplayOptionListener(this);
        this.newRecordPane.addDisplayOptionMouseListener(this);
        
        this.existingRecordPane.addDisplayOptionListener(this);
        this.existingRecordPane.addDisplayOptionMouseListener(this);
        
        this.toolbar.addDisplayOptionListener(this);
    }
    
    public void newRecordButton() {
        if (this.getCurrentView() != this.newRecordPane) {
            this.setCurrentView(this.newRecordPane);
            this.engine.getGUI().validateState();
        }
    }
    
    public void loadRecordButton() {
        if (this.getCurrentView() != this.existingRecordPane) {
            SessionData dataType = SessionData.getSessionDataFromDatabaseRecordAndQuery(this.recordType, QueryType.ALL_AVAILABLE_TO_SESSION);
            RecordsList<Record> data = this.session.checkoutSessionData(this.session.getSessionID(), dataType);

            if (data == null) {
                this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setVisible(true);
                    }
                });
                
                this.existingRecordPane.clearRecords();
                this.setCurrentView(this.existingRecordPane);
                this.engine.getGUI().validateState();
            }

            else {
                this.setCurrentView(this.existingRecordPane);
                this.existingRecordPane.insertRecords(data);
                this.engine.getGUI().validateState();
            }
        }
    }
    
    public void backButton() {
        if (this.getCurrentView() != this.currentRecordPane) {
            this.setCurrentView(this.currentRecordPane);
            this.engine.getGUI().validateState();
        }

        else {
            this.engine.sendQuitToRuntimeThread("User called exit", false);
        }
    }
    
    public void submitNewRecordButton() {
        Record record = this.newRecordPane.buildNewlySubmittedRecord();
        if (record != null) {
            LockedPacket p = new NewSessionDataPacket(this.engine, this.session, this.recordType, record, true);
            this.session.lockAndSendPacket(PacketPriority.HIGH, p);
            this.newRecordPane.clearInfo();
        }

        else {
            this.engine.info("Record was incomplete or invalid");
            this.newRecordPane.clearInfo();
        }

        this.setCurrentView(this.currentRecordPane);
        this.engine.getGUI().validateState();
    }
    
    public void submitExistingRecordButton() {
        Record record = this.existingRecordPane.getSelectedRecord();
        if (record == null) {
            this.engine.warn("No record selected");
        }

        else {
            RecordsList<Record> list = new RecordsList<Record> ();
            LockedPacket p = new SetSessionDataPacket(this.engine, this.session, SessionData.getSessionDataFromDatabaseRecordAndQuery(this.recordType, QueryType.SINGLE), list, true);
            this.session.lockAndSendPacket(PacketPriority.HIGH, p);
        }

        this.setCurrentView(this.currentRecordPane);
        this.engine.getGUI().validateState();
    }
    
    @Override
    public void bind() {
        super.bind();
        this.engine.getEventThread().addListener(ModalCloseEvent.class, this);
        this.engine.getEventThread().addListener(DataArriveEvent.class, this);
    }
    
    @Override
    public void unbind() {
        this.engine.getEventThread().removeListener(this);
        super.unbind();
    }
    
    @Override
    public void validateState() {
        if (this.currentPanel == this.currentRecordPane) {
            RecordsList<Record> list = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.getSessionDataFromDatabaseRecordAndQuery(this.recordType, QueryType.SINGLE));
            if (list == null || list.isEmpty()) {
                return;
            }
            
            this.currentRecordPane.setCurrentData(list.getFirst());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.toolbar.newRecord) {
            this.newRecordButton();
        }

        else if (evt.getSource() == this.toolbar.loadRecord) {
            this.loadRecordButton();
        }
            
        else if (evt.getSource() == this.toolbar.back) {
            this.backButton();
        }

        else if (this.newRecordPane.isEventSourceSubmitButton(evt)) {
            this.submitNewRecordButton();
        }

        else if (this.existingRecordPane.isEventSourceSubmitButton(evt)) {
            this.submitExistingRecordButton();
        }
    }
        
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
            this.submitExistingRecordButton();
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
    
    @Override
    public void changeEventReceived(EventObject event) {
        if (event instanceof ModalCloseEvent) {
            ModalCloseEvent evt = (ModalCloseEvent) event;
            if (evt.getSource() == this.dialog && evt.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit forced by user closing modal", null);
            }
        }
        
        // TODO: listen for session data changes so we can instantly update the
        // main display panel
            
        else if (event instanceof DataArriveEvent) {
            DataArriveEvent evt = (DataArriveEvent) event;
            if (((SessionData) evt.getSource()).equals(SessionData.getSessionDataFromDatabaseRecordAndQuery(this.recordType, QueryType.ALL_AVAILABLE_TO_SESSION))) {
                RecordsList<Record> data = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.getSessionDataFromDatabaseRecordAndQuery(this.recordType, QueryType.ALL_AVAILABLE_TO_SESSION));
                if (data == null) {
                    return; // TODO: Verify if there is a better way of handling this, don't want packet spam tho
                }

                this.dialog.maskCloseEvent();
                this.dialog.dispose();
                this.dialog = null;
                this.setCurrentView(this.existingRecordPane);
                this.existingRecordPane.insertRecords(data);
                this.engine.getGUI().validateState();
            }
        }
    }
}
