/* 
 * Copyright (C) 2013 Jonathan Smith
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
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.util.RuntimeType;

/**
 * RuntimeListener
 *
 * @author Jonathan Smith
 */
public class ClientMainPanelListener implements ActionListener {
    
    private DPADController controller;
    
    public ClientMainPanelListener(DPADController controller) {
        this.controller = controller;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        DPADEngine engine = this.controller.getEngine();
        if (engine == null || !(engine instanceof DPADLocalEngine)) {
            return;
        }
        
        DPADLocalEngine local = (DPADLocalEngine) engine;
        if (evt.getSource() == this.controller.getGui().clientMainToolbar.setUser) {
            local.setRuntime(RuntimeType.USER_SELECT);
        }
        
        else if (evt.getSource() == this.controller.getGui().clientMainToolbar.setExperiment) {
            local.setRuntime(RuntimeType.EXPERIMENT_SELECT);
        }
    }
}
