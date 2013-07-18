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
package net.jonathansmith.javadpad.client.threads.singlerecord.gui.pane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.common.database.Record;

/**
 *
 * @author Jon
 */
public abstract class NewRecordPane extends JPanel {
    
    public abstract void addDisplayOptionListener(ActionListener listener);
    
    public abstract void addDisplayOptionMouseListener(MouseListener listener);
    
    public abstract boolean isEventSourceSubmitButton(ActionEvent event);
    
    public abstract Record buildNewlySubmittedRecord();
    
    public abstract void clearInfo();
}
