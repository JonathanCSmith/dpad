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
package net.jonathansmith.javadpad.gui.experiment;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.gui.experiment.panel.ExistingExperimentPane;
import net.jonathansmith.javadpad.gui.experiment.panel.NewExperimentPane;
import net.jonathansmith.javadpad.gui.experiment.toolbar.ExperimentToolbar;

/**
 *
 * @author jonathansmith
 */
public class ExperimentSelect {
    
    public JPanel blankPanel;
    public NewExperimentPane newExperimentPane;
    public ExistingExperimentPane existingExperimentPane;
    public ExperimentToolbar experimentToolbar;
    public JPanel currentPanel;
    
    public ExperimentSelect() {
        this.blankPanel = new JPanel();
        this.newExperimentPane = new NewExperimentPane();
        this.existingExperimentPane = new ExistingExperimentPane();
        this.currentPanel = this.blankPanel;
    }
    
    public JPanel getCurrentView() {
        return this.currentPanel;
    }
    
    public void setCurrentView(JPanel panel) {
        this.currentPanel = panel;
    }
}
