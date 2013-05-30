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

import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.experiment.ExperimentDisplayOption;
import net.jonathansmith.javadpad.gui.client.main.ClientDisplayOption;
import net.jonathansmith.javadpad.gui.client.user.UserDisplayOption;
import net.jonathansmith.javadpad.gui.startup.StartupDisplayOption;

/**
 * Runtime
 *
 * @author Jonathan Smith
 */
public enum RuntimeType {
    RUNTIME_SELECT(false, true, new StartupDisplayOption()),
    SETUP_CLIENT(true, false, null),
    IDLE_CLIENT(false, true, new ClientDisplayOption()),
    
    USER_SELECT(false, true, new UserDisplayOption()),
    EXPERIMENT_SELECT(false, true, new ExperimentDisplayOption()),
    LOAD_AND_PROCESS(true, false, null),
    ANALYSE_AND_DISPLAY(true, false, null);
    
    private final boolean runnable;
    private final boolean displayable;
    private final DisplayOption display;
    
    private RuntimeType(boolean runnable, boolean displayable, DisplayOption display) {
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
