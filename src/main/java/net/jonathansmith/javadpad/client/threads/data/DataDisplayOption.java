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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

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
import net.jonathansmith.javadpad.common.database.Dataset;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.records.LoaderDataset;
import net.jonathansmith.javadpad.common.database.records.LoaderPluginRecord;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.plugin.PluginArriveEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.database.DataRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginUploadRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.session.NewSessionDataPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionFocusPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.PluginManagerHandler;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.database.recordaccess.QueryType;

/**
 *
 * @author Jon
 */
public class DataDisplayOption extends DisplayOption implements ActionListener, MouseListener, EventListener {

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
    public void bind() {
        super.bind();
        this.engine.getEventThread().addListener(ModalCloseEvent.class, this);
        this.engine.getEventThread().addListener(DataArriveEvent.class, this);
        this.engine.getEventThread().addListener(PluginArriveEvent.class, this);
    }

    @Override
    public void unbind() {
        this.engine.getEventThread().removeListener(this);
        super.unbind();
    }
    
    @Override
    public void validateState() {
        if (this.currentPanel == this.currentInformationPane) {
            LockedPacket p = new SetSessionFocusPacket(this.engine, this.session, SessionData.EXPERIMENT);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
            
            RecordsList<Record> list = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.EXPERIMENT);
            if (list == null || list.isEmpty()) {
                return;
            }
            
            this.currentInformationPane.setCurrentData(list.getFirst());
        }
        
        else if (this.currentPanel == this.addDataDisplay) {
            LockedPacket p = new SetSessionFocusPacket(this.engine, this.session, SessionData.LOADER_DATA);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
            
            RecordsList<Record> list = this.session.checkoutSessionData(this.session.getSessionID(), SessionData.LOADER_DATA);
            if (list == null || list.isEmpty()) {
                return;
            }
            
            this.addDataDisplay.setCurrentData(list.getFirst());
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.toolbar.addData) {
            this.displayAddData();
        }
        
        // Main transition to previous
        else if (evt.getSource() == this.toolbar.back) {
            this.returnToPreviousFromMain();
        }
        
        // AddData
        else if (evt.getSource() == this.addDataToolbar.setPlugin) {
            this.displayAvailablePlugins();
        }
        
        else if (this.pluginSelectPane.isEventSourceSubmitButton(evt)) {
            this.submitSelectedPlugin();
        }
        
        else if (evt.getSource() == this.addDataToolbar.addFiles) {
            this.addFilesToAddData();
        }
        
        else if (evt.getSource() == ((AddDataPane) this.addDataDisplay).removeFiles) {
            ((AddDataPane) this.addDataDisplay).removeSelected();
        }
        
        else if (evt.getSource() == this.addDataToolbar.back) {
            this.returnToPreviousFromAddData();
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
        if (event instanceof ModalCloseEvent) {
            ModalCloseEvent evt = (ModalCloseEvent) event;
            if (evt.getSource() == this.dialog && evt.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit forced by user closing modal", null);
            }
        }
        
        else if (event instanceof DataArriveEvent) {
            DataArriveEvent evt = (DataArriveEvent) event;
            SessionData d = (SessionData) evt.getSource();
            if (d.equals(SessionData.ALL_LOADER_PLUGINS)) {
                this.insertAvailablePlugins();
            }
            
            else if (d.equals(SessionData.LOADER_PLUGIN)) {
                this.parseSubmittedPlugin();
            }
        }
            
        else if (event instanceof PluginArriveEvent) {
            this.handlePluginDownload();
        }
    }
    
    private void displayAddData() {
        if (this.currentPanel != this.addDataDisplay) {
            // Create new Loader Data Set
            LoaderDataset data = new LoaderDataset();
            LockedPacket p = new NewSessionDataPacket(this.engine, this.session, DatabaseRecord.CURRENT_DATASET, data, true);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);

            this.setCurrentView(this.addDataDisplay);
            this.setCurrentToolbar(this.addDataToolbar);
            this.engine.getGUI().validateState();
        }
    }
    
    private void returnToPreviousFromMain() {
        if (this.currentPanel != this.currentInformationPane) {
            this.setCurrentView(this.currentInformationPane);
            this.engine.getGUI().validateState();
        }

        else {
            LockedPacket p = new SetSessionFocusPacket(this.engine, this.session, SessionData.USER);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);

            this.engine.sendQuitToRuntimeThread("User called back", false);
        }
    }
    
    private void displayAvailablePlugins() {
        if (this.currentPanel != this.pluginSelectPane) {
            Packet p = new DataRequestPacket(this.engine, this.session, SessionData.ALL_LOADER_PLUGINS);
            this.session.addPacketToSend(PacketPriority.HIGH, p);

            this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.setVisible(true);
                }
            });
        }
    }
    
    // Add in plugins that have arrived from server side to the gui
    private void insertAvailablePlugins() {
        RecordsList<Record> data = this.engine.getSession().checkoutSessionData(this.session.getSessionID(), SessionData.ALL_LOADER_PLUGINS);
        if (data == null) {
            return;
        }

        this.dialog.maskCloseEvent();
        this.dialog.dispose();
        this.dialog = null;

        this.pluginSelectPane.clearRecords();
        this.pluginSelectPane.insertRecords(data);
        this.setCurrentView(this.pluginSelectPane);
        this.engine.getGUI().validateState();
    }
    
    // Handle when a user selects their desired plugin
    private void submitSelectedPlugin() {
        Record selection = this.pluginSelectPane.getSelectedRecord();
        if (selection != null && selection instanceof PluginRecord) {
            RecordsList<Record> list = new RecordsList<Record> ();
            list.add(selection);
            
            PluginManagerHandler manager = this.engine.getPluginManager();
            
            // If we have it already (exact match), just set the session data
            if (manager.getLoaderPluginRecordList().contains(selection)) {
                LockedPacket p = new SetSessionDataPacket(this.engine, this.session, SessionData.getSessionDataFromDatabaseRecordAndQuery(DatabaseRecord.LOADER_PLUGIN, QueryType.SINGLE), list, false);
                this.session.lockAndSendPacket(PacketPriority.HIGH, p);

                this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setVisible(true);
                    }
                });
            }
            
            // Otherwise default to server side, if local is newer, the user should have uploaded it!
            else {
                LockedPacket p = new PluginUploadRequestPacket(this.engine, this.session, (byte) 1, (PluginRecord) selection, false);
                this.session.lockAndSendPacket(PacketPriority.HIGH, p);
                
                this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setVisible(true);
                    }
                });
            }
            
            this.localVersion = (PluginRecord) selection;
            this.pluginSelectPane.clearRecords();
            this.engine.getGUI().validateState();
        }
        
        else {
            this.engine.info("Record was incomplete or invalid");
            this.pluginSelectPane.clearRecords();
        }
    }
    
    // Handle when a plugin has been successfully downloaded
    private void handlePluginDownload() {
        RecordsList<Record> list = new RecordsList<Record> ();
        list.add(this.localVersion);
        LockedPacket p = new SetSessionDataPacket(this.engine, this.session, SessionData.LOADER_PLUGIN, list, false);
        this.session.lockAndSendPacket(PacketPriority.HIGH, p);

        this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(true);
            }
        });
    }
    
    // Handle when the plugin has been successfully set
    private void parseSubmittedPlugin() {
        RecordsList<Record> data = this.engine.getSession().checkoutSessionData(this.session.getSessionID(), SessionData.LOADER_PLUGIN);
        if (data == null) {
            return;
        }

        this.dialog.maskCloseEvent();
        this.dialog.dispose();
        this.dialog = null;

        this.pluginSelectPane.clearRecords();
        this.setCurrentView(this.currentInformationPane);
        this.setCurrentToolbar(this.toolbar);
        this.engine.getGUI().validateState();
    }
    
    private void addFilesToAddData() {
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
    
    private void returnToPreviousFromAddData() {
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
}