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
package net.jonathansmith.javadpad.client.threads.plugin.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.EventObject;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.gui.dialogs.PopupDialog;
import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.client.threads.plugin.gui.pane.PluginSelectPane;
import net.jonathansmith.javadpad.client.threads.plugin.gui.toolbar.PluginSelectToolbar;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginTransferPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.PluginManager;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class PluginUploadDisplayOption extends DisplayOption implements ActionListener, MouseListener, ChangeListener {

    public PluginSelectPane pluginSelectPane;
    public PluginSelectToolbar pluginSelectToolbar;
    
    private WaitForRecordsDialog dialog = null;
    private PluginRecord localVersion = null;
    
    public PluginUploadDisplayOption() {
        super();
        this.pluginSelectPane = new PluginSelectPane();
        this.pluginSelectToolbar = new PluginSelectToolbar();
        this.currentPanel = this.pluginSelectPane;
        this.currentToolbar = this.pluginSelectToolbar;
        
        this.pluginSelectToolbar.findLoaders.addActionListener(this);
        this.pluginSelectToolbar.findAnalysers.addActionListener(this);
        this.pluginSelectToolbar.back.addActionListener(this);
        this.pluginSelectPane.jList1.addMouseListener(this);
        this.pluginSelectPane.submit.addActionListener(this);
    }
    
    public void displayLoaders() {
        this.displayPlugins(false);
    }
    
    public void displayAnalysers() {
        this.displayPlugins(true);
    }
    
    public void displayPlugins(boolean type) {
        PluginManager manager = this.engine.getPluginManager();
        RecordsList<Record> list = manager.getLocalPluginRecordList(type);
        this.pluginSelectPane.insertRecords(list);
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
    public void setEngine(Client engine, ClientSession session) {
        super.setEngine(engine, session);
        this.session.addListener(this);
    }
    
    @Override
    public void validateState() {}

    @Override
    public void actionPerformed(ActionEvent evt) {
        Client client = (Client) this.engine;
        ClientSession session = client.getSession();
        
        if (this.dialog != null) {
            return;
        }
        
        if (evt.getSource() == this.pluginSelectToolbar.findLoaders) {
            this.displayLoaders();
        }
        
        else if (evt.getSource() == this.pluginSelectToolbar.findAnalysers) {
            this.displayAnalysers();
        }
        
        else if (evt.getSource() == this.pluginSelectToolbar.back) {
            this.engine.sendQuitToRuntimeThread("User called back", false);
        }
        
        else if (evt.getSource() == this.pluginSelectPane.submit) {
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
    public void changeEventReceived(EventObject event) {
        if (event instanceof ModalCloseEvent) {
            ModalCloseEvent evt = (ModalCloseEvent) event;
            if (evt.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit forced by user closing modal", null);
            }
        }
        
        else if (event instanceof DataArriveEvent) {
            if (this.dialog == null) {
                return; // Not waiting
            }
            
            RecordsList<Record> data = this.engine.getSession().checkoutData(SessionData.PLUGIN);
            if (data == null) {
                return;
            }
            
            this.dialog.maskCloseEvent();
            this.dialog.dispose();
            this.dialog = null;
            
            final JDialog popupDialog;
            if (data.isEmpty() || data.getFirst() == null) {
                String pluginPath = this.engine.getPluginManager().getPluginPath(this.localVersion.getName());
                LockedPacket p = new PluginTransferPacket(this.engine, this.session, this.localVersion, pluginPath);
                this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
                
                popupDialog = new PopupDialog(new JFrame(), "Uploading plugin to server, it is advised not to use this plugin for some time");
                // TODO: progressbar + blocking?
                // OR Asynchronous + popup inform when done
            }
            
            else if (this.localVersion.equals(data.getFirst())) {
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
            
            this.pluginSelectPane.clearRecords();
            this.setCurrentView(this.pluginSelectPane);
            this.engine.getGUI().validateState();
        }
    }
}