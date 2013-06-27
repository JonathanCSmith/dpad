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
package net.jonathansmith.javadpad.client.threads.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.EventObject;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.jonathansmith.javadpad.client.gui.dialogs.PopupDialog;
import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.CurrentRecordPane;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.ExistingRecordPane;
import net.jonathansmith.javadpad.client.threads.data.pane.AddDataPane;
import net.jonathansmith.javadpad.client.threads.data.pane.DataPane;
import net.jonathansmith.javadpad.client.threads.data.pane.PluginSelectPane;
import net.jonathansmith.javadpad.client.threads.data.toolbar.AddDataToolbar;
import net.jonathansmith.javadpad.client.threads.data.toolbar.DataToolbar;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.records.LoaderPluginRecord;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.database.DataRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginTransferPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class DataDisplayOption extends DisplayOption implements ActionListener, MouseListener, ChangeListener {

    private WaitForRecordsDialog dialog = null;
    
    // Main
    public CurrentRecordPane currentInformationPane;
    public DataToolbar toolbar;
    
    // AddData
    public CurrentRecordPane addDataDisplay;
    public AddDataToolbar addDataToolbar;
    
    // Plugin select - subsidiary of AddData
    public ExistingRecordPane pluginSelectPane;
    private PluginRecord localVersion = null;
    
    public DataDisplayOption() {
        super();
        this.currentInformationPane = new DataPane();
        this.toolbar = new DataToolbar();
        
        this.addDataDisplay = new AddDataPane();
        this.addDataToolbar = new AddDataToolbar();
        
        this.pluginSelectPane = new PluginSelectPane();
        
        this.addListeners();
        
        this.currentPanel = this.currentInformationPane;
        this.currentToolbar = this.toolbar;
    }
    
    private void addListeners() {
        this.toolbar.addDisplayOptionListener(this);
        
        this.addDataToolbar.addDisplayOptionListener(this);
        
        this.pluginSelectPane.addDisplayOptionListener(this);
        this.pluginSelectPane.addDisplayOptionMouseListener(this);
    }
    
    public void submitSelectedPlugin() {
        Record selection = this.pluginSelectPane.getSelectedRecord();
        if (selection != null && selection instanceof PluginRecord) {
            LockedPacket p = new SetSessionDataPacket(this.engine, this.session, DatabaseRecord.PLUGIN, selection);
            this.session.lockAndSendPacket(PacketPriority.HIGH, p);
            
            this.dialog = new WaitForRecordsDialog(new JFrame(), true);
            this.dialog.addListener(this);
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.setVisible(true);
                }
            });
            
            this.localVersion = (PluginRecord) selection;
            this.pluginSelectPane.clearRecords();
            this.engine.getGUI().validateState();
        }
        
        else {
            this.engine.info("Record was incomplete or invalid");
            this.pluginSelectPane.clearRecords();
        }
    }
    
    @Override
    public void validateState() {
        if (this.currentPanel == this.currentInformationPane) {
            Record record = this.engine.getSession().getKeySessionData(DatabaseRecord.EXPERIMENT);
            this.currentInformationPane.setCurrentData(record);
        }
        
        else if (this.currentPanel == this.addDataDisplay) {
            Record record = this.engine.getSession().getKeySessionData(DatabaseRecord.CURRENT_DATASET);
            this.addDataDisplay.setCurrentData(record);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        // Main
        if (evt.getSource() == this.toolbar.addData) {
            if (this.currentPanel != this.addDataDisplay) {
                // Create new Loader Data Set
                
                this.setCurrentView(this.addDataDisplay);
                this.setCurrentToolbar(this.addDataToolbar);
                this.engine.getGUI().validateState();
            }
        }
        
        else if (evt.getSource() == this.toolbar.back) {
            if (this.currentPanel != this.currentInformationPane) {
                this.setCurrentView(this.currentInformationPane);
                this.engine.getGUI().validateState();
            }
            
            else {
                this.engine.sendQuitToRuntimeThread("User called back", false);
            }
        }
        
        // AddData
        else if (evt.getSource() == this.addDataToolbar.setPlugin) {
            if (this.currentPanel != this.pluginSelectPane) {
                Packet p = new DataRequestPacket(this.engine, this.session, SessionData.ALL_PLUGINS);
                this.session.addPacketToSend(PacketPriority.HIGH, p);
                
                this.dialog = new WaitForRecordsDialog(new JFrame(), true);
                this.dialog.addListener(this);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setVisible(true);
                    }
                });
            }
        }
        
        else if (evt.getSource() == this.addDataToolbar.back) {
            if (this.currentPanel != this.addDataDisplay) {
                this.setCurrentView(this.addDataDisplay);
                this.engine.getGUI().validateState();
            }
            
            else {
                this.setCurrentView(this.currentInformationPane);
                this.setCurrentToolbar(this.toolbar);
                this.engine.getGUI().validateState();
            }
        }
        
        else if (this.pluginSelectPane.isEventSourceSubmitButton(evt)) {
            this.submitSelectedPlugin();
        }
    }

    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
            if (this.currentPanel == this.pluginSelectPane) {
                this.submitSelectedPlugin();
            }
        }
    }

    public void mousePressed(MouseEvent me) {}

    public void mouseReleased(MouseEvent me) {}

    public void mouseEntered(MouseEvent me) {}

    public void mouseExited(MouseEvent me) {}

    // Listen for session events etc, forward to plugin
    public void changeEventReceived(EventObject event) {
        if (this.dialog == null) {
            return; // Not waiting for anything!
        }
        
        if (event instanceof ModalCloseEvent) {
            ModalCloseEvent evt = (ModalCloseEvent) event;
            if (evt.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit forced by user closing modal", null);
            }
        }
        
        else if (event instanceof DataArriveEvent) {
            DataArriveEvent evt = (DataArriveEvent) event;
            if (((SessionData) evt.getSource()).equals(SessionData.ALL_PLUGINS)) {
                // TODO: Only need LOADER plugins here... - just cycle and remove? would allow us to check versions too....
                
                RecordsList<Record> data = this.engine.getSession().checkoutData(SessionData.ALL_PLUGINS);
                if (data == null) {
                    return;
                }
                
                RecordsList<Record> local = this.engine.getPluginManager().getLocalPluginRecordList();
                for (Record record : data) {
                    if (!local.contains(record)) {
                        local.add(record);
                    }
                }
                
                RecordsList<Record> finalList = new RecordsList<Record> ();
                for (Record record : local) {
                    if (record instanceof LoaderPluginRecord) {
                        finalList.add(record);
                    }
                }
                
                // TODO: name and version check?
                
                this.pluginSelectPane.clearRecords();
                this.pluginSelectPane.insertRecords(finalList);
                this.setCurrentView(this.pluginSelectPane);
                this.engine.getGUI().validateState();
            }
            
            else if (((SessionData) evt.getSource()).equals(SessionData.PLUGIN_STATUS)) {
                RecordsList<Record> data = this.engine.getSession().checkoutData(SessionData.PLUGIN);
                if (data == null || !(data.getFirst() instanceof IntegerRecord)) {
                    return;
                }
                
                this.dialog.maskCloseEvent();
                this.dialog.dispose();
                this.dialog = null;
                
                IntegerRecord res = (IntegerRecord) data.getFirst();
                final JDialog popupDialog;
                if (res.getValue() == 1) {
                    String pluginPath = this.engine.getPluginManager().getPluginPath(this.localVersion.getName());
                    LockedPacket p = new PluginTransferPacket(this.engine, this.session, this.localVersion, pluginPath);
                    this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);

                    popupDialog = new PopupDialog(new JFrame(), "Uploading plugin to server, it is advised not to use this plugin for some time");
                    // TODO: progressbar + blocking?
                    // OR Asynchronous + popup inform when done
                    // TODO: set session data!
                }

                else if (res.getValue() == 0) {
                    popupDialog = new PopupDialog(new JFrame(), "Server already has this plugin, you can use immediately");
                }

                else {
                    popupDialog = new PopupDialog(new JFrame(), "Server has a newer version of this plugin, downloading now, it is advised not to use this plugin for some time");
                    // TODO: progressbar + blocking?
                    // OR Asynchronous + popup inform when done
                }
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        popupDialog.setVisible(true);
                    }
                });

                this.setCurrentView(this.addDataDisplay);
                this.engine.getGUI().validateState();
            }
        }
    }
}
