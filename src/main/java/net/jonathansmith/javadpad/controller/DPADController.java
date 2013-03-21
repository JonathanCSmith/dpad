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

package net.jonathansmith.javadpad.controller;

import net.jonathansmith.javadpad.controller.listener.DatabaseListener;
import net.jonathansmith.javadpad.controller.listener.RuntimeListener;
import net.jonathansmith.javadpad.controller.listener.UserListener;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.gui.DPADGui;
import net.jonathansmith.javadpad.util.DPADLogger;

/**
 * DPADController
 *
 * @author Jonathan Smith
 */
public class DPADController implements Runnable {
    
    public final DPADLogger logger;
    public final DPADEngine engine;
    public final DPADGui gui;
    
    public boolean errored = false;
    
    public DPADController(DPADLogger logger, DPADEngine engine, DPADGui gui) {
        this.logger = logger;
        this.engine = engine;
        this.gui = gui;
    }
    
    public void init() {
        this.gui.addRuntimeListener(new RuntimeListener(this));
        this.gui.addDatabaseListener(new DatabaseListener(this));
        this.gui.addUserRuntimeListener(new UserListener(this));
    }
    
    @Override
    public void run() {
        
    }

    public DPADEngine getEngine() {
        return this.engine;
    }
    
    public DPADGui getGui() {
        return this.gui;
    }
}
