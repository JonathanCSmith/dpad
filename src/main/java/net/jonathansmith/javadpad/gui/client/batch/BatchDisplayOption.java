/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.gui.client.batch;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.database.batch.Batch;
import net.jonathansmith.javadpad.database.batch.BatchManager;
import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.database.experiment.ExperimentManager;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.batch.panel.DisplayBatchPane;
import net.jonathansmith.javadpad.gui.client.batch.panel.ExistingBatchPane;
import net.jonathansmith.javadpad.gui.client.batch.panel.NewBatchPane;
import net.jonathansmith.javadpad.gui.client.batch.toolbar.BatchToolbar;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public class BatchDisplayOption extends DisplayOption implements MouseListener {
    
    public DisplayBatchPane displayPanel;
    public NewBatchPane newBatchPane;
    public ExistingBatchPane existingBatchPane;
    public BatchToolbar batchToolbar;
    
    public BatchDisplayOption() {
        super();
        this.displayPanel = new DisplayBatchPane();
        this.newBatchPane = new NewBatchPane();
        this.existingBatchPane = new ExistingBatchPane();
        this.batchToolbar = new BatchToolbar();
        this.currentPanel = this.displayPanel;
        this.currentToolbar = this.batchToolbar;
        
        this.batchToolbar.newEntry.addActionListener(this);
        this.batchToolbar.loadEntry.addActionListener(this);
        this.batchToolbar.back.addActionListener(this);
        this.newBatchPane.submit.addActionListener(this);
        this.existingBatchPane.submit.addActionListener(this);
        this.existingBatchPane.jList1.addMouseListener(this);
    }
    
    @Override
    public void setCurrentView(JPanel panel) {
        super.setCurrentView(panel);
        if (panel instanceof DisplayBatchPane) {
            Batch batch = this.controller.getSessionBatch();
            this.displayPanel.setCurrentBatch(batch);
        }
    }

    @Override
    public void validateState(DPADController controlller) {}

    public void actionPerformed(ActionEvent evt) {
        if (this.controller.engine == null || !(this.controller.engine instanceof DPADLocalEngine)) {
            return;
        }
        
        Experiment experiment = this.controller.getSessionExperiment();
        
        if (evt.getSource() == this.batchToolbar.newEntry) {
            if (!(this.getCurrentView() instanceof NewBatchPane)) {
                this.setCurrentView(this.newBatchPane);
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == this.batchToolbar.loadEntry) {
            if (!(this.getCurrentView() instanceof ExistingBatchPane)) {
                if (experiment != null) {
                    this.setCurrentView(this.existingBatchPane);
                    this.existingBatchPane.insertData(experiment.getBatches());
                    this.controller.getGui().validateState();
                }
            }
        }
        
        else if (evt.getSource() == this.batchToolbar.back) {
            if (this.getCurrentView() instanceof NewBatchPane || this.getCurrentView() instanceof ExistingBatchPane) {
                this.setCurrentView(this.displayPanel);
                this.controller.getGui().validateState();
            }
            
            else {
                this.controller.getEngine().sendQuitToRuntime();
            }
        }
        
        else if (evt.getSource() == this.newBatchPane.submit) {
            if (experiment == null) {
                return;
            }
            
            String name = this.newBatchPane.name.getText();
            this.newBatchPane.name.setText("");
            
            String description = this.newBatchPane.description.getText();
            this.newBatchPane.description.setText("");
            
            if (!name.contentEquals("") && !description.contentEquals("")) {
                Batch batch = new Batch();
                batch.setName(name);
                batch.setDescription(description);
                
                BatchManager.getInstance().saveNew(batch);
                
                experiment.addBatch(batch);
                ExperimentManager.getInstance().save(experiment);
                
                this.controller.setSessionBatch(batch);
            } 
            
            else {
                DPADLogger.warning("Some fields were incomplete, returning. Your entry was not saved");
            }
            
            this.setCurrentView(this.displayPanel);
            this.controller.getGui().validateState();
        }
        
        else if (evt.getSource() == this.existingBatchPane.submit) {
            Batch batch = this.existingBatchPane.getSelectedBatch();
            
            if (batch == null) {
                DPADLogger.warning("No experiment selected, returning to main user screen.");
            }
            
            else {
                this.controller.setSessionBatch(batch);
            }
            
            this.setCurrentView(this.displayPanel);
            this.controller.getGui().validateState();
        }
    }

    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
            Batch batch = this.existingBatchPane.getSelectedBatch();
            
            if (batch != null) {
                this.controller.setSessionBatch(batch);
            }
            
            this.setCurrentView(this.displayPanel);
            this.controller.getGui().validateState();
        }
    }

    public void mousePressed(MouseEvent me) {}

    public void mouseReleased(MouseEvent me) {}

    public void mouseEntered(MouseEvent me) {}

    public void mouseExited(MouseEvent me) {}
}
