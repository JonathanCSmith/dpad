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
package net.jonathansmith.javadpad.gui.client.experiment;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.database.experiment.ExperimentManager;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.database.user.UserManager;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.experiment.panel.DisplayExperimentPane;
import net.jonathansmith.javadpad.gui.client.experiment.panel.ExistingExperimentPane;
import net.jonathansmith.javadpad.gui.client.experiment.panel.NewExperimentPane;
import net.jonathansmith.javadpad.gui.client.experiment.toolbar.ExperimentToolbar;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public class ExperimentDisplayOption extends DisplayOption implements MouseListener {
    
    public DisplayExperimentPane displayPanel;
    public NewExperimentPane newExperimentPane;
    public ExistingExperimentPane existingExperimentPane;
    public ExperimentToolbar experimentToolbar;
    
    public ExperimentDisplayOption() {
        super();
        this.displayPanel = new DisplayExperimentPane();
        this.newExperimentPane = new NewExperimentPane();
        this.existingExperimentPane = new ExistingExperimentPane();
        this.experimentToolbar = new ExperimentToolbar();
        this.currentPanel = this.displayPanel;
        this.currentToolbar = this.experimentToolbar;
        
        this.experimentToolbar.newEntry.addActionListener(this);
        this.experimentToolbar.loadEntry.addActionListener(this);
        this.experimentToolbar.back.addActionListener(this);
        this.newExperimentPane.submit.addActionListener(this);
        this.existingExperimentPane.submit.addActionListener(this);
        this.existingExperimentPane.jList1.addMouseListener(this);
    }
    
    @Override
    public void setCurrentView(JPanel panel) {
        super.setCurrentView(panel);
        if (panel instanceof DisplayExperimentPane) {
            Experiment experiment = this.controller.getSessionExperiment();
            this.displayPanel.setCurrentExperiment(experiment);
        }
    }

    @Override
    public void validateState(DPADController controlller) {}

    public void actionPerformed(ActionEvent evt) {DPADEngine engine = this.controller.getEngine();
        if (engine == null || !(engine instanceof DPADLocalEngine)) {
            return;
        }
        
        User user = this.controller.getSessionUser();
        
        // User wants to create a new experiment, do nothing to the database
        if (evt.getSource() == this.experimentToolbar.newEntry) {
            if (!(this.getCurrentView() instanceof NewExperimentPane)) {
                this.setCurrentView(this.newExperimentPane);
                this.controller.getGui().validateState();
            }
        }
        
        // User wants to load an experiment, load all valid experiments
        else if (evt.getSource() == this.experimentToolbar.loadEntry) {
            if (!(this.getCurrentView() instanceof ExistingExperimentPane)) {
                if (user != null) {
                    this.setCurrentView(this.existingExperimentPane);
                    this.existingExperimentPane.insertData(user.getExperiments());
                    this.controller.getGui().validateState();
                }
            }
        }
        
        // User wants to return
        else if (evt.getSource() == this.experimentToolbar.back) {
            if (this.getCurrentView() instanceof NewExperimentPane || this.currentPanel instanceof ExistingExperimentPane) {
                this.setCurrentView(this.displayPanel);
                this.controller.getGui().validateState();
            }
                
            else {
                this.controller.getEngine().sendQuitToRuntime();
            }
            
        }
        
        // User has created a new experiment and now wants to save it, create
        // the experiment, add experiment id to user and save all
        else if (evt.getSource() == this.newExperimentPane.submit) {
            if (user == null) {
                return;
            }
            
            String name = this.newExperimentPane.name.getText();
            this.newExperimentPane.name.setText("");
            
            String description = this.newExperimentPane.description.getText();
            this.newExperimentPane.description.setText("");
            
            if (!name.contentEquals("") && !description.contentEquals("")) {
                Experiment experiment = new Experiment();
                experiment.setName(name);
                experiment.setDescription(description);
                
                ExperimentManager.getInstance().saveNew(experiment);
                
                user.addExperiment(experiment);
                UserManager.getInstance().save(user);
                
                this.controller.setSessionExperiment(experiment);
            } 
            
            else {
                DPADLogger.warning("Some fields were incomplete, returning. Your entry was not saved");
            }
            
            this.setCurrentView(this.displayPanel);
            this.controller.getGui().validateState();
        }
        
        // User wants to load the existing experiment, set the experiment to the session
        else if (evt.getSource() == this.existingExperimentPane.submit) {
            Experiment experiment = this.existingExperimentPane.getSelectedExperiment();
            
            if (experiment == null) {
                DPADLogger.warning("No experiment selected, returning to main user screen.");
            } 
            
            else {
                this.controller.setSessionExperiment(experiment);
            }
            
            this.setCurrentView(this.displayPanel);
            this.controller.getGui().validateState();
        }
    }

    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
            Experiment experiment = this.existingExperimentPane.getSelectedExperiment();
            
            if (experiment != null) {
                this.controller.setSessionExperiment(experiment);
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
