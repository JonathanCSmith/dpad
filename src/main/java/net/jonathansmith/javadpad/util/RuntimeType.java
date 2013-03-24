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

/**
 * Runtime
 *
 * @author Jonathan Smith
 */
public enum RuntimeType {
    RUNTIME_SELECT(false, true),
    SETUP_LOCAL(true, true),
    IDLE_LOCAL(false, true),
    
    USER_SELECT(true, true),
    LOAD_AND_PROCESS(true, true),
    ANALYSE_AND_DISPLAY(true, true);
    
    private final boolean runnable;
    private final boolean displayable;
    
    private RuntimeType(boolean runnable, boolean displayable) {
        this.runnable = runnable;
        this.displayable = displayable;
    }
    
    public boolean isRunnable() {
        return this.runnable;
    }
    
    public boolean isDisplayable() {
        return this.displayable;
    }
}
