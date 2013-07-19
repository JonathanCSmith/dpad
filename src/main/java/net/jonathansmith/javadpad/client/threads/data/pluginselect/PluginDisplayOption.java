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
package net.jonathansmith.javadpad.client.threads.data.pluginselect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.threads.ClientRuntimeThread;
import net.jonathansmith.javadpad.client.threads.data.pluginselect.pane.PluginSelectPane;
import net.jonathansmith.javadpad.client.threads.uploadplugin.gui.toolbar.PluginSelectToolbar;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import static net.jonathansmith.javadpad.common.database.DatabaseRecord.ANALYSER_PLUGIN;
import static net.jonathansmith.javadpad.common.database.DatabaseRecord.LOADER_PLUGIN;
import net.jonathansmith.javadpad.common.database.Dataset;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.events.DPADEvent;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.plugin.PluginArriveEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.session.RequestSessionDataPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.UploadPluginRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.PluginManagerHandler;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.database.recordaccess.QueryType;

/**
 *
 * @author Jon
 */
public class PluginDisplayOption extends DisplayOption implements ActionListener, MouseListener, EventListener{
    
    public PluginSelectPane currentInformationPane;
    public PluginSelectToolbar toolbar;
    
    private DatabaseRecord pluginType = null;
    private WaitForRecordsDialog dialog = null;
    private PluginRecord localVersion;
    
    public PluginDisplayOption() {
        super();
        this.currentInformationPane = new PluginSelectPane();
        this.toolbar = new PluginSelectToolbar();
        
        this.addListeners();
        
        this.currentPanel = this.currentInformationPane;
        this.currentToolbar = this.toolbar;
    }
    
    private void addListeners() {
        this.currentInformationPane.addDisplayOptionListener(this);
        this.currentInformationPane.addDisplayOptionMouseListener(this);
    }
    
    public void setPluginType(DatabaseRecord pluginType) {
        if (pluginType != DatabaseRecord.LOADER_PLUGIN || pluginType != DatabaseRecord.ANALYSER_PLUGIN) {
            this.pluginType = DatabaseRecord.LOADER_PLUGIN;
        }
        
        else {
            this.pluginType = pluginType;
        }
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
    public void validateState() {}
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.toolbar.addPlugin) {
            this.displayAvailablePlugins();
        }
        
        else if (evt.getSource() == this.toolbar.back) {
            this.handleShutdown();
        }
        
        else if (evt.getSource() == this.currentInformationPane.submit) {
            this.submitSelectedPlugin();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
            this.submitSelectedPlugin();
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
    public void changeEventReceived(DPADEvent evt) {
        if (evt instanceof ModalCloseEvent) {
            ModalCloseEvent event = (ModalCloseEvent) evt;
            if (event.getSource() == this.dialog && event.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit was forced by user closing modal", null);
            }
        }
        
        else if (evt instanceof DataArriveEvent) {
            DataArriveEvent event = (DataArriveEvent) evt;
            SessionData arriveType = (SessionData) evt.getSource();
            
            if (arriveType == SessionData.ALL_LOADER_PLUGINS || arriveType == SessionData.ALL_ANALYSER_PLUGINS) {
                this.insertAvailablePlugins();
            }
            
            else if (arriveType == SessionData.LOADER_PLUGIN || arriveType == SessionData.ANALYSER_PLUGIN) {
                this.parseSubmittedPlugin();
            }
        }
        
        else if (evt instanceof PluginArriveEvent) {
            this.handlePluginDownloaded();
        }
    }
    
    private void displayAvailablePlugins() {
        Packet p = new RequestSessionDataPacket(this.engine, this.session, SessionData.getSessionDataFromDatabaseRecordAndQuery(this.pluginType, QueryType.ALL_AVAILABLE_TO_SESSION));
        this.session.addPacketToSend(PacketPriority.HIGH, p);

        this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(true);
            }
        });
    }
    
    private void insertAvailablePlugins() {
        RecordsList<Record> data = this.engine.getSession().softlyCheckoutSessionData(SessionData.ALL_LOADER_PLUGINS);
        if (data == null || data.isEmpty()) {
            return;
        }

        this.dialog.maskCloseEvent();
        this.dialog.dispose();
        this.dialog = null;

        this.currentInformationPane.clearRecords();
        this.currentInformationPane.insertRecords(data);
        this.engine.getGUI().validateState();
    }
    
    private void submitSelectedPlugin() {
        Record selection = this.currentInformationPane.getSelectedRecord();
        if (selection != null && selection instanceof PluginRecord) {
            RecordsList<Record> list = new RecordsList<Record> ();
            list.add(selection);
            
            PluginManagerHandler manager = this.engine.getPluginManager();
            
            // If we have it already (exact match), just set the session data
            if (manager.getLoaderPluginRecordList().contains(selection)) {
                LockedPacket p = new SetSessionDataPacket(this.engine, this.session, SessionData.getSessionDataFromDatabaseRecordAndQuery(this.pluginType, QueryType.SINGLE), list, false);
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
                LockedPacket p = new UploadPluginRequestPacket(this.engine, this.session, (byte) 1, (PluginRecord) selection, false);
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
            this.currentInformationPane.clearRecords();
            this.engine.getGUI().validateState();
        }
        
        else {
            this.engine.info("Record was incomplete or invalid");
            this.currentInformationPane.clearRecords();
        }
    }
    
    private void handlePluginDownloaded() {
        RecordsList<Record> list = new RecordsList<Record> ();
        list.add(this.localVersion);
        LockedPacket p = new SetSessionDataPacket(this.engine, this.session, SessionData.getSessionDataFromDatabaseRecordAndQuery(this.pluginType, QueryType.SINGLE), list, false);
        this.session.lockAndSendPacket(PacketPriority.HIGH, p);

        this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(true);
            }
        });
    }
    
    private void parseSubmittedPlugin() {
        RecordsList<Record> data = this.engine.getSession().softlyCheckoutSessionData(SessionData.getSessionDataFromDatabaseRecordAndQuery(this.pluginType, QueryType.SINGLE));
        if (data == null || data.isEmpty()) {
            return;
        }
        
        RecordsList<Record> focusList = this.session.getSessionFocusData();
        if (focusList != null && !focusList.isEmpty() && focusList.getFirst() instanceof Dataset) {
            Dataset focus = (Dataset) focusList.getFirst();
            focus.setPluginInfo((PluginRecord) data.getFirst());
        }

        this.dialog.maskCloseEvent();
        this.dialog.dispose();
        this.dialog = null;

        this.currentInformationPane.clearRecords();
        this.handleShutdown();
    }
    
    private void handleShutdown() {
        switch (this.pluginType) {
            case LOADER_PLUGIN:
                this.engine.forceSetRuntime(ClientRuntimeThread.ADD_DATA, true);
                return;

            case ANALYSER_PLUGIN:
                this.engine.forceSetRuntime(ClientRuntimeThread.ANALYSE_DATA, true);
                return;
        }
    }
}