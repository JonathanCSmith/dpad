package jonathansmith.dpad.client.engine.executor;

import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Plugin task class to be implemented by all plugin tasks.
 */
public class PluginTask extends Task {

    private final IPluginTask    plugin_task;
    private final IPluginRuntime plugin_runtime;

    public PluginTask(String taskName, ClientEngine engine, IPluginTask pluginTask, IPluginRuntime runtime) {
        super("Plugin Task: " + taskName, engine);

        this.plugin_task = pluginTask;
        this.plugin_runtime = runtime;
    }

    @Override
    protected void runTask() {
        this.plugin_task.runTask(this.plugin_runtime);
    }

    @Override
    protected void killTask() {
        this.plugin_task.killTask(this.plugin_runtime);
    }
}
