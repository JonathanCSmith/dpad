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

package net.jonathansmith.javadpad.toberefactored.controller;

import java.awt.EventQueue;

import net.jonathansmith.javadpad.common.database.Batch;
import net.jonathansmith.javadpad.common.database.Experiment;
import net.jonathansmith.javadpad.common.database.User;
import net.jonathansmith.javadpad.toberefactored.engine.DPADClientEngine;
import net.jonathansmith.javadpad.toberefactored.engine.DPADEngine;
import net.jonathansmith.javadpad.toberefactored.engine.host.DPADHostEngine;
import net.jonathansmith.javadpad.toberefactored.gui.DPADGui;
import net.jonathansmith.javadpad.toberefactored.util.logging.DPADLogger;

/**
 * DPADController
 *
 * @author Jonathan Smith
 */
public class DPADController extends Thread {
    
    public final DPADLogger logger;
    public final DPADGui gui;
    
    public boolean errored = false;
    public boolean initialised = false;
    public DPADEngine engine = null;
    
    public DPADController() {
        this.logger = new DPADLogger();
        this.gui = new DPADGui(this);
    }
    
    public void init() {
        this.gui.init();
        EventQueue.invokeLater(this.gui);
        this.initialised = true;
    }
    
    @Override
    public void run() {
        try {
            this.logger.info("Threads started, idling in main");
            while (!this.errored || !this.gui.isShowing()) {
                Thread.sleep(100);
                
                // Should do error checking and handling here, specific to each
                // child thread
            }
            
        } catch (InterruptedException ex) {
            this.logger.severe("Main thread interrupted, program will exit");
        }
    }

    public DPADGui getGui() {
        return this.gui;
    }

    public DPADEngine getEngine() {
        return this.engine;
    }
    
    public void setEngine(DPADEngine eng) {
        if (this.engine != null || eng == null) {
            this.logger.warning("Cannot change the DPAD engine once it has been set");
            return;
        }
        
        this.engine = eng;
        this.gui.setEngine(eng);
        this.engine.init();
        (new Thread(this.engine)).start();
    }
    
    public User getSessionUser() {
        if (this.engine instanceof DPADHostEngine) {
            return null;
        }
        
        DPADClientEngine client = (DPADClientEngine) this.engine;
        return client.getUser();
    }
    
    public void setSessionUser(User user) {
        if (!(this.engine instanceof DPADHostEngine)) {
            DPADClientEngine client = (DPADClientEngine) this.engine;
            client.setUser(user);
        }
    }
    
    public Experiment getSessionExperiment() {
        if (this.engine instanceof DPADHostEngine) {
            return null;
        }
        
        DPADClientEngine client = (DPADClientEngine) this.engine;
        return client.getExperiment();
    }
    
    public void setSessionExperiment(Experiment experiment) {
        if (!(this.engine instanceof DPADHostEngine)) {
            DPADClientEngine client = (DPADClientEngine) this.engine;
            client.setExperiment(experiment);
        }
    }
    
    public Batch getSessionBatch() {
        if (this.engine instanceof DPADHostEngine) {
            return null;
        }
        
        DPADClientEngine client = (DPADClientEngine) this.engine;
        return client.getBatch();
    }
    
    public void setSessionBatch(Batch batch) {
        if (!(this.engine instanceof DPADHostEngine)) {
            DPADClientEngine client = (DPADClientEngine) this.engine;
            client.setBatch(batch);
        }
    }
}
