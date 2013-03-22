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

package net.jonathansmith.javadpad.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DPADLogger
 *
 * @author Jonathan Smith
 */
public class DPADLogger {
    
    private Logger dpadLogger;
    
    public DPADLogger() {
        this.dpadLogger = Logger.getLogger("DPAD");
    }
    
    public void info(String msg) {
        this.dpadLogger.log(Level.INFO, "[DPAD] {0}", msg);
    }
    
    public void warning(String msg) {
        this.dpadLogger.log(Level.WARNING, "[DPAD] {0}", msg);
    }
    
    public void severe(String msg) {
        this.dpadLogger.log(Level.SEVERE, "[DPAD] {0}", msg);
    }
    
    public Logger getLogger() {
        return this.dpadLogger;
    }
    
    public void addLogger(Handler handler) {
        this.dpadLogger.addHandler(handler);
    }
}
