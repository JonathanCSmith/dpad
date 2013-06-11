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

package net.jonathansmith.javadpad.common.util.threads;

import net.jonathansmith.javadpad.client.gui.DisplayOption;
import net.jonathansmith.javadpad.client.gui.batch.BatchDisplayOption;
import net.jonathansmith.javadpad.client.gui.experiment.ExperimentDisplayOption;
import net.jonathansmith.javadpad.client.gui.loadandprocess.LoadProcessDisplayOption;
import net.jonathansmith.javadpad.client.gui.main.ClientDisplayOption;
import net.jonathansmith.javadpad.client.gui.user.UserDisplayOption;

/**
 * Runtime
 *
 * @author Jonathan Smith
 */
public enum RuntimeThread {
    SETUP_CLIENT(true, false, null),
    IDLE_CLIENT(false, true, new ClientDisplayOption()),
    
    USER_SELECT(false, true, new UserDisplayOption()),
    EXPERIMENT_SELECT(false, true, new ExperimentDisplayOption()),
    BATCH_SELECT(false, true, new BatchDisplayOption()),
    
    LOAD_AND_PROCESS(true, true, new LoadProcessDisplayOption()),
    ANALYSE_AND_DISPLAY(true, false, null);
    
    private final boolean runnable;
    private final boolean displayable;
    private final DisplayOption display;
    
    private RuntimeThread(boolean runnable, boolean displayable, DisplayOption display) {
        this.runnable = runnable;
        this.displayable = displayable;
        this.display = display;
    }
    
    public boolean isRunnable() {
        return this.runnable;
    }
    
    public boolean isDisplayable() {
        return this.displayable;
    }
    
    public DisplayOption getDisplay() {
        return this.display;
    }
}
