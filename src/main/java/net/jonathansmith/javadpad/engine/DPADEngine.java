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
package net.jonathansmith.javadpad.engine;

import java.util.Observable;

import net.jonathansmith.javadpad.util.RuntimeType;
import net.jonathansmith.javadpad.util.DPADLogger;
import net.jonathansmith.javadpad.util.ThreadType;

/**
 *
 * @author Jon
 */
public abstract class DPADEngine extends Observable implements Runnable {
    
    public DPADLogger logger;
    
    public DPADEngine(DPADLogger logger) {
        this.logger = logger;
    }
    
    public abstract void init();
    
    public abstract ThreadType getThreadType();
    public abstract RuntimeType getCurrentRuntime();
    public abstract void sendQuitToRuntime();
    public abstract void quitEngine();
    public abstract void runtimeFinished(boolean status);
}
