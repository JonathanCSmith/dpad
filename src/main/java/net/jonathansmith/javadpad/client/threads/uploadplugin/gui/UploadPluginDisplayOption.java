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
package net.jonathansmith.javadpad.client.threads.uploadplugin.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.jonathansmith.javadpad.api.database.PluginRecord;
import net.jonathansmith.javadpad.api.database.Record;
import net.jonathansmith.javadpad.api.events.Event;
import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.gui.dialogs.PopupDialog;
import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.network.session.ClientSession;
import net.jonathansmith.javadpad.client.threads.uploadplugin.gui.pane.PluginDisplayPane;
import net.jonathansmith.javadpad.client.threads.uploadplugin.gui.toolbar.PluginAddToolbar;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.gui.DisplayOption;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.dummyrecords.IntegerRecord;
import net.jonathansmith.javadpad.common.network.packet.plugins.UploadPluginPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.UploadPluginRequestPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.PluginManagerHandler;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class UploadPluginDisplayOption extends DisplayOption implements ActionListener, EventListener {

    public PluginDisplayPane pluginSelectPane;
    public PluginAddToolbar pluginSelectToolbar;
    
    private WaitForRecordsDialog dialog = null;
    private PluginRecord localVersion = null;
    
    public UploadPluginDisplayOption() {
        super();
        this.pluginSelectPane = new PluginDisplayPane();
        this.pluginSelectToolbar = new PluginAddToolbar();
        this.currentPanel = this.pluginSelectPane;
        this.currentToolbar = this.pluginSelectToolbar;
        
        this.pluginSelectToolbar.addPlugin.addActionListener(this);
        this.pluginSelectToolbar.back.addActionListener(this);
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
            filepath = chooser.getSelectedFile().getAbsoluteFile();

            String name = filepath.getName().substring(0, filepath.getName().length() - 4);
            PluginManagerHandler manager = this.engine.getPluginManager();
            manager.addPluginFile(filepath);
            
            PluginRecord newPlugin = null;
            while (newPlugin == null) {
                try {
                    Thread.sleep(100);
                }
                
                catch (InterruptedException ex) {
                    // TODO:
                }
                
                newPlugin = manager.getPluginRecord(name);
            }
            
            this.pluginSelectPane.setChosenPlugin(newPlugin);
            LockedPacket p = new UploadPluginRequestPacket(this.engine, this.session, (byte) 0, newPlugin, true);
            this.session.lockAndSendPacket(PacketPriority.HIGH, p);

            this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);
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
            this.engine.returnToDefaultRuntime("User called back", false);
        }
    }

    @Override
    public void changeEventReceived(Event event) {
        if (event instanceof ModalCloseEvent) {
            ModalCloseEvent evt = (ModalCloseEvent) event;
            if (evt.getSource() == this.dialog && evt.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit forced by user closing modal", null);
            }
        }
        
        else if (event instanceof DataArriveEvent) {
            DataArriveEvent evt = (DataArriveEvent) event;
            if (((SessionData) evt.getSource()).equals(SessionData.PLUGIN_STATUS)) {
                RecordsList<Record> data = this.session.getSessionData(this.session.getSessionID(), SessionData.PLUGIN_STATUS, false);
                if (data != null && data.getFirst() instanceof IntegerRecord) {
                    if (this.dialog != null) {
                        this.dialog.maskCloseEvent();
                        this.dialog.dispose();
                        this.dialog = null;
                    }
                    
                    IntegerRecord res = (IntegerRecord) data.getFirst();
                    final JDialog popupDialog;
                    if (res.getValue() == 1) {
                        String pluginPath = this.engine.getPluginManager().getPluginPath(this.localVersion.getName());
                        LockedPacket p = new UploadPluginPacket(this.engine, this.session, this.localVersion, pluginPath);
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
                    
                    try {
                        Thread.sleep(100);
                    }
                    
                    catch (InterruptedException ex) {
                        // TODO:
                    }
                    
                    popupDialog.dispose();

                    this.setCurrentView(this.pluginSelectPane);
                    this.engine.getGUI().validateState();
                }
            }
        }
    }
}