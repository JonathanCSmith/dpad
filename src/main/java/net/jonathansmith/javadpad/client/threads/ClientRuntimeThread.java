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

import net.jonathansmith.javadpad.client.gui.displayoptions.DisplayOption;
import net.jonathansmith.javadpad.client.gui.displayoptions.RecordsDisplayOption;
import net.jonathansmith.javadpad.client.threads.experiment.gui.pane.CurrentExperimentPane;
import net.jonathansmith.javadpad.client.threads.experiment.gui.pane.ExistingExperimentPane;
import net.jonathansmith.javadpad.client.threads.experiment.gui.pane.NewExperimentPane;
import net.jonathansmith.javadpad.client.threads.runtimeselect.gui.RuntimeSelectDisplayOption;
import net.jonathansmith.javadpad.client.threads.startup.gui.StartupDisplayOption;
import net.jonathansmith.javadpad.client.threads.user.gui.pane.CurrentUserPane;
import net.jonathansmith.javadpad.client.threads.user.gui.pane.ExistingUserPane;
import net.jonathansmith.javadpad.client.threads.user.gui.pane.NewUserPane;
import net.jonathansmith.javadpad.common.network.session.DatabaseRecord;
import net.jonathansmith.javadpad.common.threads.RunnableThread;
import net.jonathansmith.javadpad.common.threads.RuntimeThread;

/**
 * Runtime
 *
 * @author Jonathan Smith
 */
public enum ClientRuntimeThread implements RuntimeThread {
    STARTUP(false, true, new StartupDisplayOption(), null),
    RUNTIME_SELECT(false, true, new RuntimeSelectDisplayOption(), null),
    USER(false, true, new RecordsDisplayOption(DatabaseRecord.USER, new CurrentUserPane(), new NewUserPane(), new ExistingUserPane(), "User toolbar:"), null),
    EXPERIMENT(false, true, new RecordsDisplayOption(DatabaseRecord.EXPERIMENT, new CurrentExperimentPane(), new NewExperimentPane(), new ExistingExperimentPane(), "Experiment toolbar:"), null);
    
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
