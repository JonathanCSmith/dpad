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
package net.jonathansmith.javadpad.util.log;

import java.io.IOException;
import java.io.OutputStream;

import java.util.logging.Level;

/**
 *
 * @author Jon
 */
public class LoggerOutputStream extends OutputStream {

    public DPADLogger logger;
    public Level level;
    private String mem;
    
    public LoggerOutputStream(DPADLogger logger, Level level) {
        this.logger = logger;
        this.level = level;
        this.mem = "";
    }
    
    @Override
    public void write(int i) throws IOException {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (i & 0xff);
        mem = mem + new String(bytes);
        
        if (mem.endsWith("\n")) {
            mem = mem.substring(0, mem.length() - 1);
            flush();
        }
    }
    
    @Override
    public void flush() {
        //logger.log(level, mem);
        mem = "";
    }
}
