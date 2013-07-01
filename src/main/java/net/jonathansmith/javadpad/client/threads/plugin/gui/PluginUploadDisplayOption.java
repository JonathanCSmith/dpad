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

import java.io.File;

import java.util.EventObject;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.gui.dialogs.PopupDialog;
import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.client.threads.plugin.gui.pane.PluginDisplayPane;
import net.jonathansmith.javadpad.client.threads.plugin.gui.toolbar.PluginSelectToolbar;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginTransferPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginUploadRequestPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.PluginManager;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class PluginUploadDisplayOption extends DisplayOption implements ActionListener, ChangeListener {

    public PluginDisplayPane pluginSelectPane;
    public PluginSelectToolbar pluginSelectToolbar;
    
    private WaitForRecordsDialog dialog = null;
    private PluginRecord localVersion = null;
    
    public PluginUploadDisplayOption() {
        super();
        this.pluginSelectPane = new PluginDisplayPane();
        this.pluginSelectToolbar = new PluginSelectToolbar();
        this.currentPanel = this.pluginSelectPane;
        this.currentToolbar = this.pluginSelectToolbar;
        
        this.pluginSelectToolbar.addPlugin.addActionListener(this);
        this.pluginSelectToolbar.back.addActionListener(this);
    }
    
    public void addPlugin() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Please select your plugin");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                return !f.getName().toLowerCase().endsWith(".zip") ? f.getName().toLowerCase().endsWith(".jar") : true;
            }

            @Override
            public String getDescription() {
                return "*.jar || *.zip";
            }
        });
        
        int outcome = chooser.showOpenDialog(this.engine.getGUI());
        File filepath;
        if (outcome == JFileChooser.APPROVE_OPTION) {
            filepath = chooser.getSelectedFile();

            String name = filepath.getName().substring(0, filepath.getName().length() - 4);
            PluginManager manager = this.engine.getPluginManager();
            manager.addOrUpdatePlugin(name, filepath.getAbsolutePath(), false);
            PluginRecord newPlugin = manager.getLocalPluginRecord(name);
            if (newPlugin == null) {
                this.engine.warn("There was an error injecting your plugin");
                return;
            }
            
            this.pluginSelectPane.setChosenPlugin(newPlugin);
            LockedPacket p = new PluginUploadRequestPacket(this.engine, this.session, (byte) 0, newPlugin);
            this.session.lockAndSendPacket(PacketPriority.HIGH, p);

            this.dialog = new WaitForRecordsDialog(new JFrame(), true);
            this.dialog.addListener(this);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.setVisible(true);
                }
            });

            this.localVersion = (PluginRecord) newPlugin;
            this.engine.getGUI().validateState();
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
        
        if (evt.getSource() == this.pluginSelectToolbar.addPlugin) {
            this.addPlugin();
        }
        
        else if (evt.getSource() == this.pluginSelectToolbar.back) {
            this.engine.sendQuitToRuntimeThread("User called back", false);
        }
    }

    @Override
    public void changeEventReceived(EventObject event) {
        if (this.dialog == null) {
            return; // We are not waiting for anything!
        }
        
        
        if (event instanceof ModalCloseEvent) {
            ModalCloseEvent evt = (ModalCloseEvent) event;
            if (evt.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit forced by user closing modal", null);
            }
        }
        
        else if (event instanceof DataArriveEvent) {
            DataArriveEvent evt = (DataArriveEvent) event;
            if (((SessionData) evt.getSource()).equals(SessionData.PLUGIN_STATUS)) {
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

                this.setCurrentView(this.pluginSelectPane);
                this.engine.getGUI().validateState();
            }
        }
    }
}