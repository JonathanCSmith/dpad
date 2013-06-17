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
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.ExistingRecordPane;
import net.jonathansmith.javadpad.client.gui.displayoptions.pane.NewRecordPane;
import net.jonathansmith.javadpad.client.threads.data.toolbar.DataToolbar;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.ChangeSender;

/**
 *
 * @author Jon
 */
public class DataDisplayOption extends DisplayOption implements ActionListener, MouseListener, ChangeListener, ChangeSender {

    public CurrentRecordPane currentInformationPane;
    public NewRecordPane newBatchPane;
    public ExistingRecordPane existingBatchPane;
    public NewRecordPane newDataPane;
    public ExistingRecordPane existingDataPane;
    //public PluginDefferedPane processingPane; Might not be the way to do this, as plugins may have multiple panes and toolbars
    //public PluginDefferedPane analysingPane;
    
    public DataToolbar toolbar;
    
    public DataDisplayOption() {
        super();
        this.currentInformationPane = new CurrentDataInformationPane();
        this.newBatchPane = new NewBatchPane();
        this.existingBatchPane = new ExistingBatchPane();
        this.newDataPane = new NewDataPane();
        this.existingDataPane = new ExistingDataPane();
        //this.processingPane = new ProcessDataPane();
        //this.analysingPane = new AnalyseDataPane();
        
        this.toolbar = new DataToolbar();
        
        this.addListeners();
        
        this.currentPanel = this.currentInformationPane;
        this.currentToolbar = this.toolbar;
    }
    
    private void addListeners() {
        this.newBatchPane.addDisplayOptionListener(this);
        this.newBatchPane.addDisplayOptionMouseListener(this);
        this.existingBatchPane.addDisplayOptionListener(this);
        this.existingBatchPane.addDisplayOptionMouseListener(this);
        
        this.newDataPane.addDisplayOptionListener(this);
        this.newDataPane.addDisplayOptionMouseListener(this);
        this.existingDataPane.addDisplayOptionListener(this);
        this.existingDataPane.addDisplayOptionMouseListener(this);
        
        this.toolbar.addDisplayOptionListener(this);
    }
    
    @Override
    public void validateState() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
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
