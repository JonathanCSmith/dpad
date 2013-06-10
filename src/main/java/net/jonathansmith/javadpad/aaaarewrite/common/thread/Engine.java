/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.aaaarewrite.common.thread;

import net.jonathansmith.javadpad.aaaarewrite.DPADNew;

/**
 *
 * @author jonathansmith
 */
public abstract class Engine implements Runnable {
    
    public final DPADNew main;
    
    public boolean isAlive = false;
    public boolean errored = false;
    
    public String hostName;
    public int portNumber;
    
    public Engine(DPADNew main, String hostName, int portNumber) {
        this.main = main;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }
    
    public abstract void init();
    
    public abstract boolean isRunning();
    
    public abstract boolean isViable();
    
    public abstract void saveAndShutdown();
    
    public abstract void forceShutdown();
}
