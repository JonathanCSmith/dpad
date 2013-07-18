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
package net.jonathansmith.javadpad.client.threads.data.add;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.jonathansmith.javadpad.DPAD;
import net.jonathansmith.javadpad.client.gui.dialogs.WaitForRecordsDialog;
import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.threads.ClientRuntimeThread;
import net.jonathansmith.javadpad.client.threads.data.add.pane.AddDataPane;
import net.jonathansmith.javadpad.client.threads.data.add.toolbar.AddDataToolbar;
import net.jonathansmith.javadpad.client.threads.data.pluginselect.PluginDisplayOption;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.Dataset;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.records.LoaderDataset;
import net.jonathansmith.javadpad.common.database.records.LoaderPluginRecord;
import net.jonathansmith.javadpad.common.events.DPADEvent;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.gui.ModalCloseEvent;
import net.jonathansmith.javadpad.common.events.plugin.PluginFinishEvent;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.database.NewDataPacket;
import net.jonathansmith.javadpad.common.network.packet.database.UpdateDataPacket;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.DPADPlugin;
import net.jonathansmith.javadpad.common.threads.RuntimeThread;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public class AddDataDisplayOption extends DisplayOption implements ActionListener, EventListener {
    
    public AddDataPane currentInformationPane;
    public AddDataToolbar toolbar;
    
    private WaitForRecordsDialog dialog = null;
    private boolean runningPlugin = false;
    private LoaderDataset data;
    
    public AddDataDisplayOption() {
        super();
        this.currentInformationPane = new AddDataPane();
        this.toolbar = new AddDataToolbar();
        
        this.addListeners();
        
        this.currentPanel = this.currentInformationPane;
        this.currentToolbar = this.toolbar;
    }
    
    private void addListeners() {
        this.toolbar.addDisplayOptionListener(this);
        this.currentInformationPane.addDisplayOptionListener(this);
    }
    
    @Override
    public void bind() {
        super.bind();
        this.engine.getEventThread().addListener(ModalCloseEvent.class, this);
        this.engine.getEventThread().addListener(DataArriveEvent.class, this);
        this.engine.getEventThread().addListener(PluginFinishEvent.class, this);
    }
    
    @Override
    public void unbind() {
        if (!runningPlugin) {
            this.engine.getEventThread().removeListenerFromEvent(ModalCloseEvent.class, this);
            this.engine.getEventThread().removeListenerFromEvent(DataArriveEvent.class, this);
        }
        
        else {
            this.engine.getEventThread().removeListener(this);
        }
        
        super.unbind();
    }
    
    @Override
    public void validateState() {
        RecordsList<Record> list = this.session.getSessionFocusData();
        LoaderDataset r;
        if (list != null && !list.isEmpty() && list.getFirst() instanceof LoaderDataset) {
            r = (LoaderDataset) list.getFirst();
            this.currentInformationPane.setCurrentData(r);
            this.handleButtonStates(r);
        }
        
        else {
            LoaderDataset newData = new LoaderDataset();
            LockedPacket p = new NewDataPacket(this.engine, this.session, newData, true);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
            
            this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.setVisible(true);
                }
            });
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.toolbar.setPlugin) {
            PluginDisplayOption displayOption = (PluginDisplayOption) ClientRuntimeThread.DISPLAY_PLUGINS.getDisplay();
            displayOption.setPluginType(DatabaseRecord.LOADER_PLUGIN);
            this.engine.forceSetRuntime(ClientRuntimeThread.DISPLAY_PLUGINS, true);
        }
        
        else if (evt.getSource() == this.toolbar.addFiles) {
            this.addFilesToData();
        }
        
        else if (evt.getSource() == this.currentInformationPane.removeFiles) {
            this.currentInformationPane.removeSelected();
        }
        
        else if (evt.getSource() == this.toolbar.run) {
            this.runPluginClientSide();
        }
        
        else if (evt.getSource() == this.toolbar.submit) {
            this.runPluginServerSide();
        }
        
        else if (evt.getSource() == this.toolbar.back) {
            this.engine.forceSetRuntime(ClientRuntimeThread.DATA, true);
        }
    }
    
    @Override
    public void changeEventReceived(DPADEvent evt) {
        if (evt instanceof ModalCloseEvent) {
            ModalCloseEvent event = (ModalCloseEvent) evt;
            if (event.getSource() == this.dialog && event.getWasForcedClosed()) {
                this.engine.forceShutdown("Early exit forced by user closing modal", null);
            }
        }
        
        else if (evt instanceof DataArriveEvent) {
            DataArriveEvent event = (DataArriveEvent) evt;
            SessionData arrivedType = (SessionData) event.getSource();
            
            if (arrivedType == SessionData.LOADER_DATA) {
                RecordsList<Record> result = this.session.softlyCheckoutSessionData(arrivedType);
                if (result == null || result.isEmpty() || !(result.getFirst() instanceof LoaderDataset)) {
                    return;
                }
                
                LoaderDataset record = (LoaderDataset) result.getFirst();
                this.currentInformationPane.setCurrentData(record);
                this.handleButtonStates(record);
                this.data = record;
                
                if (this.dialog != null) {
                    this.dialog.maskCloseEvent();
                    this.dialog.dispose();
                    this.dialog = null;
                }
            }
        }
        
        else if (evt instanceof PluginFinishEvent && this.runningPlugin) {
            Dataset result = (Dataset) ((PluginFinishEvent) evt).getSource();
            if (!(result instanceof LoaderDataset)) {
                return;
            }
            
            this.handlePluginFinishOnClient((LoaderDataset) result);
        }
    }
    
    private void handleButtonStates(LoaderDataset r) {
        if (r.getPluginInfo() == null) {
            this.toolbar.setPlugin.setEnabled(true);
            this.toolbar.back.setEnabled(true);

            this.toolbar.addFiles.setEnabled(false);
            this.currentInformationPane.removeFiles.setEnabled(false);
            this.toolbar.run.setEnabled(false);
            this.toolbar.submit.setEnabled(false);
        }

        else if (!r.getHasBeenClientProcessed()) {
            this.toolbar.setPlugin.setEnabled(true);
            this.toolbar.addFiles.setEnabled(true);
            this.currentInformationPane.removeFiles.setEnabled(true);
            this.toolbar.run.setEnabled(true);
            this.toolbar.back.setEnabled(true);

            this.toolbar.submit.setEnabled(false);
        }

        else if (!r.getHasBeenServerProcessed()) {
            this.toolbar.submit.setEnabled(true);
            this.toolbar.back.setEnabled(true);

            this.toolbar.setPlugin.setEnabled(false);
            this.toolbar.addFiles.setEnabled(false);
            this.currentInformationPane.setEnabled(false);
            this.toolbar.run.setEnabled(false);
        }

        else {
            this.toolbar.back.setEnabled(true);

            this.toolbar.setPlugin.setEnabled(false);
            this.toolbar.addFiles.setEnabled(false);
            this.currentInformationPane.setEnabled(false);
            this.toolbar.run.setEnabled(false);
            this.toolbar.submit.setEnabled(false);
        }
    }
    
    private void addFilesToData() { 
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Please select your data");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        RecordsList<Record> list = this.session.getSessionFocusData();
        if (list == null || list.isEmpty()) {
            return;
        }

        Record record = list.getFirst();
        if (!(record instanceof Dataset)) {
            return;
        }

        Dataset d = (Dataset) record;

        final Set<String> ext = ((LoaderPluginRecord) d.getPluginInfo()).getAllowedExtensions();
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

            RecordsList<Record> lister = this.session.getSessionFocusData();
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
    
    private void runPluginClientSide() {
        this.data = this.currentInformationPane.getCurrentData();
        PluginRecord record = this.data.getPluginInfo();
        DPADPlugin plugin = this.engine.getPluginManager().getPlugin(record.getName());
        RuntimeThread clientThread = plugin.getRuntimeThread(DPAD.Platform.CLIENT);
        this.runningPlugin = true;
        this.engine.forceSetRuntime(clientThread, true);
    }
    
    private void handlePluginFinishOnClient(LoaderDataset result) {
        this.engine.getEventThread().removeListenerFromEvent(PluginFinishEvent.class, this); /* Just so we dont have multiple, not sure what would happen :S */
        this.engine.forceSetRuntime(ClientRuntimeThread.ADD_DATA, true);
        this.runningPlugin = false;
        
        if (result == null) {
            this.currentInformationPane.setCurrentData(this.data);
        }
        
        else {
            /* TODO: Need to move away from new */
            LockedPacket p = new UpdateDataPacket(this.engine, this.session, this.data, true);
            this.session.lockAndSendPacket(PacketPriority.MEDIUM, p);
            
            this.dialog = new WaitForRecordsDialog(new JFrame(), this.engine, true);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.setVisible(true);
                }
            });
        }
    }
    
    private void runPluginServerSide() {
        // TODO: Send server run command
        // TODO: get from focus, that way packet is generic for plugin types
        // TODO: check focus, get plugin bind and run
    }
}