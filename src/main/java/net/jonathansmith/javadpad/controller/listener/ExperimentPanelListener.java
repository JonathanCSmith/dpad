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
import net.jonathansmith.javadpad.gui.experiment.panel.ExistingExperimentPane;
import net.jonathansmith.javadpad.gui.experiment.panel.NewExperimentPane;
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
        if (evt.getSource() == this.controller.getGui().experimentSelect.experimentToolbar.newExperiment) {
            if (!(this.controller.getGui().experimentSelect.getCurrentView() instanceof NewExperimentPane)) {
                this.controller.getGui().experimentSelect.setCurrentView(this.controller.getGui().experimentSelect.newExperimentPane);
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == this.controller.getGui().experimentSelect.experimentToolbar.loadExperiment) {
            if (!(this.controller.getGui().experimentSelect.getCurrentView() instanceof ExistingExperimentPane)) {
                this.controller.getGui().experimentSelect.setCurrentView(this.controller.getGui().experimentSelect.existingExperimentPane);
                this.controller.getGui().experimentSelect.existingExperimentPane.insertData(ExperimentManager.getInstance().loadExperiments());
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == this.controller.getGui().experimentSelect.experimentToolbar.experimentBack) {
            if (this.controller.getGui().experimentSelect.getCurrentView() instanceof NewExperimentPane) {
                this.controller.getGui().experimentSelect.setCurrentView(this.controller.getGui().experimentSelect.blankPanel);
                this.controller.getGui().validateState();
            }
            
            else if (this.controller.getGui().experimentSelect.currentPanel instanceof ExistingExperimentPane) {
                this.controller.getGui().experimentSelect.setCurrentView(this.controller.getGui().experimentSelect.blankPanel);
                this.controller.getGui().validateState();
                
            } else {
                this.controller.getEngine().sendQuitToRuntime();
            }
            
        }
        
        else if (evt.getSource() == this.controller.getGui().experimentSelect.newExperimentPane.submit) {
            String name = this.controller.getGui().experimentSelect.newExperimentPane.name.getText();
            this.controller.getGui().experimentSelect.newExperimentPane.name.setText("");
            
            String description = this.controller.getGui().experimentSelect.newExperimentPane.description.getText();
            this.controller.getGui().experimentSelect.newExperimentPane.description.setText("");
            
            if (!name.contentEquals("") && !description.contentEquals("")) {
                Experiment experiment = new Experiment();
                experiment.setName(name);
                experiment.setDescription(description);
                
                ExperimentManager manager = ExperimentManager.getInstance();
                manager.saveNewExperiment(experiment);
                
            } else {
                DPADLogger.warning("Some fields were incomplete, returning. Your entry was not saved");
            }
            
            this.controller.getGui().experimentSelect.setCurrentView(this.controller.getGui().experimentSelect.blankPanel);
            this.controller.getGui().validateState();
        }
        
        else if (evt.getSource() == this.controller.getGui().experimentSelect.existingExperimentPane.submit) {
            Experiment experiment = this.controller.getGui().experimentSelect.existingExperimentPane.getSelectedExperiment();
            
            if (experiment == null) {
                DPADLogger.warning("No experiment selected, returning to main user screen.");
                
            } else {
                this.controller.setSessionExperiment(experiment);
            }
            
            this.controller.getGui().experimentSelect.setCurrentView(this.controller.getGui().experimentSelect.blankPanel);
            this.controller.getGui().validateState();
        }
    }
}
