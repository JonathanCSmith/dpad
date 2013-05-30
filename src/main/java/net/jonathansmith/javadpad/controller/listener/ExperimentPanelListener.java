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
import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.database.experiment.ExperimentManager;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.gui.client.experiment.ExperimentDisplayOption;
import net.jonathansmith.javadpad.gui.client.experiment.panel.ExistingExperimentPane;
import net.jonathansmith.javadpad.gui.client.experiment.panel.NewExperimentPane;
import net.jonathansmith.javadpad.util.RuntimeType;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public class ExperimentPanelListener implements ActionListener {
    
    public DPADController controller;
    
    public ExperimentPanelListener(DPADController controller) {
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent evt) {
        DPADEngine engine = this.controller.getEngine();
        if (engine == null || !(engine instanceof DPADLocalEngine)) {
            return;
        }
        
        ExperimentDisplayOption display = (ExperimentDisplayOption) RuntimeType.EXPERIMENT_SELECT.getDisplay();
        if (evt.getSource() == display.experimentToolbar.newExperiment) {
            if (!(display.getCurrentView() instanceof NewExperimentPane)) {
                display.setCurrentView(display.newExperimentPane);
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == display.experimentToolbar.loadExperiment) {
            if (!(display.getCurrentView() instanceof ExistingExperimentPane)) {
                display.setCurrentView(display.existingExperimentPane);
                display.existingExperimentPane.insertData(ExperimentManager.getInstance().loadAll());
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == display.experimentToolbar.experimentBack) {
            if (display.getCurrentView() instanceof NewExperimentPane) {
                display.setCurrentView(display.displayPanel);
                this.controller.getGui().validateState();
            }
            
            else if (display.currentPanel instanceof ExistingExperimentPane) {
                display.setCurrentView(display.displayPanel);
                this.controller.getGui().validateState();
                
            } else {
                this.controller.getEngine().sendQuitToRuntime();
            }
            
        }
        
        else if (evt.getSource() == display.newExperimentPane.submit) {
            String name = display.newExperimentPane.name.getText();
            display.newExperimentPane.name.setText("");
            
            String description = display.newExperimentPane.description.getText();
            display.newExperimentPane.description.setText("");
            
            if (!name.contentEquals("") && !description.contentEquals("")) {
                Experiment experiment = new Experiment();
                experiment.setName(name);
                experiment.setDescription(description);
                
                ExperimentManager manager = ExperimentManager.getInstance();
                manager.saveNew(experiment);
                this.controller.setSessionExperiment(experiment);
                
            } else {
                DPADLogger.warning("Some fields were incomplete, returning. Your entry was not saved");
            }
            
            display.setCurrentView(display.displayPanel);
            this.controller.getGui().validateState();
        }
        
        else if (evt.getSource() == display.existingExperimentPane.submit) {
            Experiment experiment = display.existingExperimentPane.getSelectedExperiment();
            
            if (experiment == null) {
                DPADLogger.warning("No experiment selected, returning to main user screen.");
                
            } else {
                this.controller.setSessionExperiment(experiment);
            }
            
            display.setCurrentView(display.displayPanel);
            this.controller.getGui().validateState();
        }
    }
}
