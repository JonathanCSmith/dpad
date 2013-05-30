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

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.experiment.panel.DisplayExperimentPane;
import net.jonathansmith.javadpad.gui.client.experiment.panel.ExistingExperimentPane;
import net.jonathansmith.javadpad.gui.client.experiment.panel.NewExperimentPane;
import net.jonathansmith.javadpad.gui.client.experiment.toolbar.ExperimentToolbar;

/**
 *
 * @author jonathansmith
 */
public class ExperimentDisplayOption extends DisplayOption {
    
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

    @Override
    public void addDisplayListener(ActionListener listener) {
        this.experimentToolbar.newEntry.addActionListener(listener);
        this.experimentToolbar.loadEntry.addActionListener(listener);
        this.experimentToolbar.back.addActionListener(listener);
        this.newExperimentPane.submit.addActionListener(listener);
        this.existingExperimentPane.submit.addActionListener(listener);
    }
}
