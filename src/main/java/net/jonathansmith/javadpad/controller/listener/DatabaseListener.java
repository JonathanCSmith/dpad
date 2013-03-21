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

import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.engine.runtime.DatabaseRuntime;
import net.jonathansmith.javadpad.engine.runtime.RuntimeType;

/**
 *
 * @author Jon
 */
public class DatabaseListener implements ActionListener {
    
    private DPADController controller;
    
    public DatabaseListener(DPADController controller) {
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.controller.gui.connectDatabase) {
            if (this.controller.engine.getCurrentRuntime() == RuntimeType.DATABASE) {
                String path = this.controller.gui.databaseURLText.getText();
                File file = new File(path);
                if (file.exists()) {
                    ((DatabaseRuntime) this.controller.engine.getRuntime()).setAttemptConnection(path);
                }
            }
        }
    }
}
