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

import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.CurrentRecordPane;
import net.jonathansmith.javadpad.client.threads.data.pane.AddDataPane;
import net.jonathansmith.javadpad.client.threads.data.pane.DataPane;
import net.jonathansmith.javadpad.client.threads.data.toolbar.AddDataToolbar;
import net.jonathansmith.javadpad.client.threads.data.toolbar.DataToolbar;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.ChangeSender;

/**
 *
 * @author Jon
 */
public class DataDisplayOption extends DisplayOption implements ActionListener, MouseListener, ChangeListener, ChangeSender {

    // Main
    public CurrentRecordPane currentInformationPane;
    public DataToolbar toolbar;
    
    // AddData
    public CurrentRecordPane addDataDisplay;
    public AddDataToolbar addDataToolbar;
    
    public DataDisplayOption() {
        super();
        this.currentInformationPane = new DataPane();
        this.toolbar = new DataToolbar();
        
        this.addDataDisplay = new AddDataPane();
        this.addDataToolbar = new AddDataToolbar();
        
        this.addListeners();
        
        this.currentPanel = this.currentInformationPane;
        this.currentToolbar = this.toolbar;
    }
    
    private void addListeners() {
        this.toolbar.addDisplayOptionListener(this);
    }
    
    
    
//    public void submitSelectedPlugin() {
//        Record selection = this.pluginSelectPane.getSelectedRecord();
//        if (selection != null && selection instanceof PluginRecord) {
//            LockedPacket p = new SetSessionDataPacket(this.engine, this.session, DatabaseRecord.PLUGIN, selection);
//            this.session.lockAndSendPacket(PacketPriority.HIGH, p);
//            
//            this.dialog = new WaitForRecordsDialog(new JFrame(), true);
//            this.dialog.addListener(this);
//            
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    dialog.setVisible(true);
//                }
//            });
//            
//            this.localVersion = (PluginRecord) selection;
//            this.pluginSelectPane.clearRecords();
//            this.engine.getGUI().validateState();
//        }
//        
//        else {
//            this.engine.info("Record was incomplete or invalid");
//            this.pluginSelectPane.clearRecords();
//        }
//    }
    
    
    @Override
    public void validateState() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.toolbar.addData) {
            if (this.currentPanel != this.addDataDisplay) {
                this.setCurrentView(this.addDataDisplay);
                this.setCurrentToolbar(this.addDataToolbar);
                this.engine.getGUI().validateState();
            }
        }
    }

    public void mouseClicked(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void mousePressed(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void mouseReleased(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void mouseEntered(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void mouseExited(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    // Listen for session events etc, forward to plugin
    public void changeEventReceived(EventObject event) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    // Inform plugins of events
    public void addListener(ChangeListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void removeListener(ChangeListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void fireChange(EventObject event) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }
}
