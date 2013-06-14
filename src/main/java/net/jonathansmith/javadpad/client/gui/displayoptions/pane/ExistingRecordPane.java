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
package net.jonathansmith.javadpad.client.gui.displayoptions.pane;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public abstract class ExistingRecordPane extends JPanel {
    
    public JButton submit = new JButton();
    
    public ExistingRecordPane() {
        this.submit.setText("Submit");
    }
    
    public void addDisplayOptionListener(ActionListener listener) {
        this.submit.addActionListener(listener);
    }
    
    public abstract void addDisplayOptionMouseListener(MouseListener listener);
    
    public abstract void insertRecords(RecordsList<Record> data);
    
    public abstract Record getSelectedRecord();
}
