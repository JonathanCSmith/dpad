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

package net.jonathansmith.javadpad.client.threads;

import net.jonathansmith.javadpad.client.gui.DisplayOption;
import net.jonathansmith.javadpad.client.threads.runtimeselect.gui.RuntimeSelectDisplayOption;
import net.jonathansmith.javadpad.client.threads.startup.gui.StartupDisplayOption;
import net.jonathansmith.javadpad.common.threads.RunnableThread;
import net.jonathansmith.javadpad.common.threads.RuntimeThread;

/**
 * Runtime
 *
 * @author Jonathan Smith
 */
public enum ClientRuntimeThread implements RuntimeThread {
    STARTUP(false, true, new StartupDisplayOption(), null),
    RUNTIME_SELECT(false, true, new RuntimeSelectDisplayOption(), null);
    
    
    // OLD: Keeping for reference TODO: Phase these out
//    
//    USER_SELECT(false, true, new UserDisplayOption()),
//    EXPERIMENT_SELECT(false, true, new ExperimentDisplayOption()),
//    BATCH_SELECT(false, true, new BatchDisplayOption()),
//    
//    LOAD_AND_PROCESS(true, true, new LoadProcessDisplayOption()),
//    ANALYSE_AND_DISPLAY(true, false, null);
    
    private final boolean runnable;
    private final boolean displayable;
    private final DisplayOption display;
    private final RunnableThread thread;
    
    private ClientRuntimeThread(boolean runnable, boolean displayable, DisplayOption display, RunnableThread thread) {
        this.runnable = runnable;
        this.displayable = displayable;
        this.display = display;
        this.thread = thread;
    }
    
    @Override
    public boolean isRunnable() {
        return this.runnable;
    }
    
    @Override
    public boolean isDisplayable() {
        return this.displayable;
    }
    
    @Override
    public DisplayOption getDisplay() {
        return this.display;
    }
    
    @Override
    public RunnableThread getThread() {
        return this.thread;
    }
}
