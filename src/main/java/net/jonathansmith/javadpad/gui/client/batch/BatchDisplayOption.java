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

import java.awt.event.ActionListener;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.batch.panel.DisplayBatchPane;
import net.jonathansmith.javadpad.gui.client.batch.panel.ExistingBatchPane;
import net.jonathansmith.javadpad.gui.client.batch.panel.NewBatchPane;
import net.jonathansmith.javadpad.gui.client.batch.toolbar.BatchToolbar;

/**
 *
 * @author jonathansmith
 */
public class BatchDisplayOption extends DisplayOption {
    
    public DisplayBatchPane displayPanel;
    public NewBatchPane newBatchPane;
    public ExistingBatchPane existingBatchPane;
    public BatchToolbar batchToolbar;
    
    public BatchDisplayOption() {
        super();
        this.displayPanel = new DisplayBatchPane();
        this.newBatchPane = new NewBatchPane();
        this.existingBatchPane = new ExistingBatchPane();
        this.currentPanel = this.displayPanel;
        this.currentToolbar = this.batchToolbar;
    }

    @Override
    public void validateState(DPADController controlller) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addDisplayListener(ActionListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
