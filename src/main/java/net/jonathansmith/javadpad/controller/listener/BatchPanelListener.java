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
package net.jonathansmith.javadpad.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.database.batch.Batch;
import net.jonathansmith.javadpad.database.batch.BatchManager;
import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.database.experiment.ExperimentManager;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.gui.client.batch.BatchDisplayOption;
import net.jonathansmith.javadpad.gui.client.batch.panel.ExistingBatchPane;
import net.jonathansmith.javadpad.gui.client.batch.panel.NewBatchPane;
import net.jonathansmith.javadpad.util.RuntimeType;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public class BatchPanelListener implements ActionListener {
    
    public DPADController controller;
    
    public BatchPanelListener(DPADController controller) {
        this.controller = controller;
    }
    
    public void actionPerformed(ActionEvent evt) {
        DPADEngine engine = this.controller.getEngine();
        if (engine == null || !(engine instanceof DPADLocalEngine)) {
            return;
        }
        
        BatchDisplayOption display = (BatchDisplayOption) RuntimeType.BATCH_SELECT.getDisplay();
        Experiment experiment = this.controller.getSessionExperiment();
        
        if (evt.getSource() == display.batchToolbar.newEntry) {
            if (!(display.getCurrentView() instanceof NewBatchPane)) {
                display.setCurrentView(display.newBatchPane);
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == display.batchToolbar.loadEntry) {
            if (!(display.getCurrentView() instanceof ExistingBatchPane)) {
                if (experiment != null) {
                    display.setCurrentView(display.existingBatchPane);
                    display.existingBatchPane.insertData(experiment.getBatches());
                    this.controller.getGui().validateState();
                }
            }
        }
        
        else if (evt.getSource() == display.batchToolbar.back) {
            if (display.getCurrentView() instanceof NewBatchPane || display.getCurrentView() instanceof ExistingBatchPane) {
                display.setCurrentView(display.displayPanel);
                this.controller.getGui().validateState();
            }
            
            else {
                this.controller.getEngine().sendQuitToRuntime();
            }
        }
        
        else if (evt.getSource() == display.newBatchPane.submit) {
            if (experiment == null) {
                return;
            }
            
            String name = display.newBatchPane.name.getText();
            display.newBatchPane.name.setText("");
            
            String description = display.newBatchPane.description.getText();
            display.newBatchPane.description.setText("");
            
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
            
            display.setCurrentView(display.displayPanel);
            this.controller.getGui().validateState();
        }
        
        else if (evt.getSource() == display.existingBatchPane.submit) {
            Batch batch = display.existingBatchPane.getSelectedBatch();
            
            if (batch == null) {
                DPADLogger.warning("No experiment selected, returning to main user screen.");
            }
            
            else {
                this.controller.setSessionBatch(batch);
            }
            
            display.setCurrentView(display.displayPanel);
            this.controller.getGui().validateState();
        }
    }
}
