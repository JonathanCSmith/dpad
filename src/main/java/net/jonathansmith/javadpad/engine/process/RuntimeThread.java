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
package net.jonathansmith.javadpad.engine.process;

import net.jonathansmith.javadpad.engine.thread.DPADLocalEngine;

/**
 *
 * @author Jon
 */
public abstract class RuntimeThread extends Thread {
    
    public DPADLocalEngine engine;
    
    public RuntimeThread(DPADLocalEngine engine) {
        this.engine = engine;
    }
    
    public abstract void init();
    public abstract void forceShutdown(boolean error);
    public void end(boolean status) {
        this.engine.runtimeFinished(status);
    }
}
