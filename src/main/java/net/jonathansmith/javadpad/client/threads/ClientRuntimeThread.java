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
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.RecordsDisplayOption;
import net.jonathansmith.javadpad.client.threads.data.add.AddDataDisplayOption;
import net.jonathansmith.javadpad.client.threads.data.overview.DataDisplayOption;
import net.jonathansmith.javadpad.client.threads.data.pluginselect.PluginDisplayOption;
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.experiment.pane.CurrentExperimentPane;
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.experiment.pane.ExistingExperimentPane;
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.experiment.pane.NewExperimentPane;
import net.jonathansmith.javadpad.client.threads.runtimeselect.gui.RuntimeSelectDisplayOption;
import net.jonathansmith.javadpad.client.threads.startup.gui.StartupDisplayOption;
import net.jonathansmith.javadpad.client.threads.uploadplugin.gui.UploadPluginDisplayOption;
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.user.pane.CurrentUserPane;
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.user.pane.ExistingUserPane;
import net.jonathansmith.javadpad.client.threads.singlerecord.gui.user.pane.NewUserPane;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.threads.RunnableThread;
import net.jonathansmith.javadpad.common.threads.RuntimeThread;

/**
 * Runtime
 *
 * @author Jonathan Smith
 */
public enum ClientRuntimeThread implements RuntimeThread {
    STARTUP(new StartupDisplayOption(), null),
    RUNTIME_SELECT(new RuntimeSelectDisplayOption(), null),
    USER(new RecordsDisplayOption(DatabaseRecord.USER, new CurrentUserPane(), new NewUserPane(), new ExistingUserPane(), "User toolbar:"), null),
    EXPERIMENT(new RecordsDisplayOption(DatabaseRecord.EXPERIMENT, new CurrentExperimentPane(), new NewExperimentPane(), new ExistingExperimentPane(), "Experiment toolbar:"), null),
    
    DATA(new DataDisplayOption(), null),
    ADD_DATA(new AddDataDisplayOption(), null),
    ANALYSE_DATA(null, null),
    
    ADD_PLUGIN(new UploadPluginDisplayOption(), null),
    DISPLAY_PLUGINS(new PluginDisplayOption(), null);
    
    private final DisplayOption display;
    private final RunnableThread thread;
    
    private ClientRuntimeThread(DisplayOption display, RunnableThread thread) {
        this.display = display;
        this.thread = thread;
    }
    
    @Override
    public boolean isRunnable() {
        return this.thread != null;
    }
    
    @Override
    public boolean isDisplayable() {
        return this.display != null;
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
