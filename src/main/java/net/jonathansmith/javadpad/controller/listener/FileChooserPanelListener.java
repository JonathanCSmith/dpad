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
package net.jonathansmith.javadpad.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.process.FileDatabaseRuntime;
import net.jonathansmith.javadpad.engine.thread.DPADLocalEngine;
import net.jonathansmith.javadpad.util.RuntimeType;
import net.jonathansmith.javadpad.util.ThreadType;

/**
 *
 * @author Jon
 */
public class FileChooserPanelListener implements ActionListener {

    public DPADController parent;
    
    public FileChooserPanelListener(DPADController controller) {
        this.parent = controller;
    }
    
    public void actionPerformed(ActionEvent evt) {
        JFileChooser fc = (JFileChooser) evt.getSource();
        String cmd = evt.getActionCommand();
        
        if (cmd.equals(JFileChooser.APPROVE_SELECTION)) {
        
            DPADEngine engine = this.parent.getEngine();
            if (engine != null && engine.getThreadType() == ThreadType.LOCAL) {
                DPADLocalEngine local = (DPADLocalEngine) engine;
                if (local.getCurrentRuntime() == RuntimeType.FILE_CONNECT) {
                    String path = fc.getSelectedFile().getAbsolutePath();
                    if (path != null && !path.contentEquals("")) {
                        FileDatabaseRuntime runtime = (FileDatabaseRuntime) local.getRuntime();
                        runtime.setAttemptConnection(path);
                    }
                }
            }
        }
    }
    
}
