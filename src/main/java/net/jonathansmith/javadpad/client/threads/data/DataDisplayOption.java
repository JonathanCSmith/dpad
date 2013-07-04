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

import java.io.File;

import java.util.EventObject;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.gui.dialogs.PopupDialog;
import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.CurrentRecordPane;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.ExistingRecordPane;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.client.threads.data.pane.AddDataPane;
import net.jonathansmith.javadpad.client.threads.data.pane.DataPane;
import net.jonathansmith.javadpad.client.threads.data.pane.PluginSelectPane;
import net.jonathansmith.javadpad.client.threads.data.toolbar.AddDataToolbar;
import net.jonathansmith.javadpad.client.threads.data.toolbar.DataToolbar;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.Dataset;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.records.LoaderDataset;
import net.jonathansmith.javadpad.common.database.records.LoaderPluginRecord;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.database.DataRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.session.NewSessionDataPacket;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginTransferPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.database.recordaccess.QueryType;

/**
 *
 * @author Jon
 */
public class DataDisplayOption extends DisplayOption implements ActionListener, MouseListener, ChangeListener {

    // TODO: Move this to separate display option logic, that way we only need
    // to poll focus for this specific pane
    
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
    
    @Override
    public void setEngine(Client engine, ClientSession session) {
        super.setEngine(engine, session);
        this.session.addListener(this);
    }
    
    public void submitSelectedPlugin() {
        Record selection = this.pluginSelectPane.getSelectedRecord();
        if (selection != null && selection instanceof PluginRecord) {
            RecordsList<Record> list = new RecordsList<Record> ();
            list.add(selection);
            
            LockedPacket p = new SetSessionDataPacket(this.engine, this.session, SessionData.getSessionDataFromDatabaseRecordAndQuery(DatabaseRecord.LOADER_PLUGIN, QueryType.SINGLE), list);
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
            RecordsList<Record> list = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.EXPERIMENT);
            if (list == null || list.isEmpty()) {
                return;
            }
            
            this.currentInformationPane.setCurrentData(list.getFirst());
        }
        
        else if (this.currentPanel == this.addDataDisplay) {
            RecordsList<Record> list = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.FOCUS);
            if (list == null || list.isEmpty()) {
                return;
            }
            
            this.addDataDisplay.setCurrentData(list.getFirst());
        }
    }

    public void actionPerformed(ActionEvent evt) {
        // Main
        if (evt.getSource() == this.toolbar.addData) {
            if (this.currentPanel != this.addDataDisplay) {
                // Create new Loader Data Set
                LoaderDataset data = new LoaderDataset();
                Packet p = new NewSessionDataPacket(this.engine, this.session, DatabaseRecord.CURRENT_DATASET, data);
                this.session.addPacketToSend(PacketPriority.MEDIUM, p);
                
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
                Packet p = new DataRequestPacket(this.engine, this.session, SessionData.ALL_LOADER_PLUGINS);
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
        
        else if (evt.getSource() == this.addDataToolbar.addFiles) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Please select your data");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            RecordsList<Record> list = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.FOCUS);
            if (list == null || list.isEmpty()) {
                return;
            }
            
            Record record = list.getFirst();
            if (!(record instanceof Dataset)) {
                return;
            }
            
            Dataset data = (Dataset) record;
            
            final Set<String> ext = ((LoaderPluginRecord) data.getPluginInfo()).getAllowedExtensions();
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    
                    String name = f.getName().toLowerCase();
                    for (String ex : ext) {
                        if (name.endsWith(ex)) {
                            return true;
                        }
                    }
                    
                    return false;
                }
                
                @Override
                public String getDescription() {
                    return "plugin specific file filter";
                }
            });
            
            int outcome = chooser.showOpenDialog(this.engine.getGUI());
            File[] files;
            if (outcome == JFileChooser.APPROVE_OPTION) {
                files = chooser.getSelectedFiles();
                
                RecordsList<Record> lister = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.FOCUS);
                if (lister == null || lister.isEmpty()) {
                    return;
                }

                Record recorder = lister.getFirst();
                if (!(record instanceof LoaderDataset)) {
                    return;
                }

                LoaderDataset dataer = (LoaderDataset) recorder;
                for (File f : files) {
                    dataer.addSourceFile(f.getAbsolutePath());
                }
            }
            
            this.engine.getGUI().validateState();
        }
        
        else if (evt.getSource() == ((AddDataPane) this.addDataDisplay).removeFiles) {
            ((AddDataPane) this.addDataDisplay).removeSelected();
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
            if (((SessionData) evt.getSource()).equals(SessionData.ALL_LOADER_PLUGINS)) {
                RecordsList<Record> data = this.engine.getSession().checkoutSessionData(this.session.getSessionID(), SessionData.ALL_LOADER_PLUGINS);
                if (data == null) {
                    return;
                }
                
                this.dialog.maskCloseEvent();
                this.dialog.dispose();
                this.dialog = null;
                
                RecordsList<Record> local = this.engine.getPluginManager().getLoaderPluginRecordList();
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
                RecordsList<Record> data = this.engine.getSession().checkoutSessionData(this.session.getSessionID(), SessionData.PLUGIN);
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
