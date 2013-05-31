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

package net.jonathansmith.javadpad.engine.local.process;

import java.util.LinkedList;
import java.util.List;

import net.jonathansmith.javadpad.database.batch.Batch;
import net.jonathansmith.javadpad.database.dataset.DataSet;
import net.jonathansmith.javadpad.engine.common.process.RuntimeProcess;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.plugin.ILoadPlugin;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 * LPRuntime
 *
 * @author Jonathan Smith
 */
public class LoadProcessProcess extends RuntimeProcess {
    
    public boolean running = false;
    public String path = null;
    public List<String> pendingProcess = new LinkedList<String> ();
    public List<String> pendingLoad = new LinkedList<String> ();
    
    private enum State {
        INCOMPLETE_INFORMATION,
        ADDING_DATA_TYPE,
        IDLE,
        LOADING_DATASET,
        PROCESSING_DATASET,
        ERRORED;
    }
    
    private State state = State.IDLE;
    private ILoadPlugin loadingPlugin = null;
    private DataSet data = new DataSet();
    
    public LoadProcessProcess(DPADLocalEngine engine) {
        super(engine);
    }

    @Override
    public void init() {
        this.running = true;
        Batch batch = this.engine.getBatch();
        if (batch != null) {
            if (batch.getEquipment() == null) {
                this.state = State.INCOMPLETE_INFORMATION;
            }
            
            else {
                this.state = State.IDLE;
            }
        }
        
        else {
            this.state = State.ERRORED;
        }
    }

    @Override
    public void run() {
        while (this.running) {
            switch (this.state) {
                case IDLE:
                    if (this.data.getDataType() == null) {
                        this.state = State.ADDING_DATA_TYPE;
                        continue;
                    }
                    
                    else if (!this.pendingLoad.isEmpty()) {
                        this.state = State.LOADING_DATASET;
                        continue;
                    }
                    
                    else if (!this.pendingProcess.isEmpty()) {
                        this.state = State.PROCESSING_DATASET;
                        continue;
                    }
                    
                    try {
                        Thread.sleep(100);

                    } catch (InterruptedException ex) {
                        DPADLogger.severe("Load and Process thread interrupted");
                        DPADLogger.logStackTrace(ex);
                        this.running = false;
                        this.forceShutdown(true);
                        return;
                    }
                    break;
                    
                case INCOMPLETE_INFORMATION:
                    // Need to inform the gui to change panels
                    break;
                    
                case ADDING_DATA_TYPE:
                    // Need to inform the gui to change panels
                    break;
                    
                case LOADING_DATASET:
                    // Check-out an unloaded packet
                    // Call plugin on the packet
                    // Build DataType
                    // Make progress available for possible widget?
                    // On complete add to process pending
                    // DB write
                    // Check for new
                    break;
                    
                case PROCESSING_DATASET:
                    // Check-out an unprocessed packet
                    // Call plugin on the packet
                    // Make progress available for possible widget?
                    // DB write
                    // Check for new
                    break;
            }
        }
    }

    @Override
    public void forceShutdown(boolean error) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void addDataToBeLoaded(String path) {
        if (this.pendingLoad.contains(path)) {
            DPADLogger.warning("Cannot add an existing pending dataset");
            return;
        }
        
        this.pendingLoad.add(path);
    }
    
    public String checkOutDataToBeLoaded() {
        if (!this.pendingLoad.isEmpty()) {
            return this.pendingLoad.remove(0);
        }
        
        return null;
    }
    
    private void addDataToBeProcessed(String path) {
        if (this.pendingProcess.contains(path)) {
            DPADLogger.warning("Cannot add an existing pending dataset");
            return;
        }
        
        this.pendingProcess.add(path);
    }
    
    public String checkOutDataToBeProcessed() {
        if (!this.pendingProcess.isEmpty()) {
            return this.pendingProcess.remove(0);
        }
        
        return null;
    }
}
