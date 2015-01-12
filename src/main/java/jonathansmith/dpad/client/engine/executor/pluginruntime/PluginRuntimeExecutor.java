package jonathansmith.dpad.client.engine.executor.pluginruntime;

import java.util.LinkedList;

import jonathansmith.dpad.api.database.PluginRecord;
import jonathansmith.dpad.api.plugins.IPlugin;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.dpad.common.engine.executor.Executor;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.executor.PluginTask;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Universal plugin plugin_runtime executor
 */
public class PluginRuntimeExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Plugin Runtime: ";

    private final PluginRecord   plugin_record;
    private final IPlugin        plugin;
    private final IPluginRuntime plugin_runtime;

    public PluginRuntimeExecutor(ClientEngine engine, PluginRecord pluginRecord) {
        super(EXECUTOR_NAME + pluginRecord.getPluginName(), engine, false);

        this.plugin_record = pluginRecord;
        this.plugin = ((ClientEngine) this.engine).getPluginManager().getPlugin(this.plugin_record.getPluginName());
        this.plugin_runtime = new PluginRuntime(engine);

        this.addTask(new PluginRuntimeSetupTask((ClientEngine) this.engine));

        LinkedList<IPluginTask> tasks = this.plugin.getPluginRuntimeTasks();
        if (tasks != null && tasks.size() > 0) {
            for (IPluginTask task : tasks) {
                PluginTask pluginTask = new PluginTask(task.getTaskName(), engine, task, this.plugin_runtime);
                this.addTask(pluginTask);
            }
        }

        this.addTask(new PluginRuntimeFinishTask((ClientEngine) this.engine));
    }
}
