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

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Jon
 */
public class DPADFormatter extends SimpleFormatter {
    
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final Level[] LVLS = new Level[] {Level.ALL, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.INFO, Level.OFF, Level.SEVERE, Level.WARNING};
    
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        
        if (record.getLevel() == Level.OFF) {
            String msg = formatMessage(record);
            if (!msg.contentEquals("")) {
                boolean shouldShow = false;
                for (int i = 0; i < LVLS.length; i++) {
                    boolean val = msg.contains(LVLS[i].getLocalizedName());
                    if (val) {
                        shouldShow = true;
                        break;
                    }
                }

                if (shouldShow) {
                    sb.append(msg).append(LINE_SEPARATOR);
                }
            }
            
        } else {
            sb.append(record.getLevel().getLocalizedName())
                    .append(": ")
                    .append(formatMessage(record))
                    .append(LINE_SEPARATOR);
        }
        
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
                
            } catch (Exception ex) {
                // Stupid
            }
        }
        
        return sb.toString();
    }
    
}
