package jonathansmith.dpad.client.engine.executor.pluginruntime;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.plugin.PluginTemporaryDisplay;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Plugin runtime setup task
 */
public class PluginRuntimeSetupTask extends Task {

    private static final String TASK_NAME = "Plugin runtime setup";

    public PluginRuntimeSetupTask(ClientEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(new PluginTemporaryDisplay((ClientEngine) this.loggingEngine)));
    }
}
