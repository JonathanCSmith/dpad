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
package net.jonathansmith.javadpad.util.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.jonathansmith.javadpad.gui.DPADGui;

/**
 *
 * @author Jon
 */
public class LogHandler extends Handler {
    
    public DPADGui gui;
    
    public LogHandler(DPADGui gui) {
        this.gui = gui;
        this.setLevel(Level.ALL);
        this.setFormatter(new DPADFormatter());
    }

    @Override
    public void publish(LogRecord lr) {
        String message = null;
        if (!this.isLoggable(lr)) {
            return;
        }
        
        try {
            message = this.getFormatter().format(lr);
            
        } catch (Exception ex) {
            System.out.println("Exception in the logger handler");
            ex.printStackTrace();
        }
        
        if (message != null || message.contentEquals("")) {
            this.gui.updateLog(message);
        }
    }

    @Override
    public void flush() {
    
    }

    @Override
    public void close() throws SecurityException {
    
    }
}
