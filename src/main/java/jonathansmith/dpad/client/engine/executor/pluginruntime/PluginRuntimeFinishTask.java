package jonathansmith.dpad.client.engine.executor.pluginruntime;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.home.ClientHomeDisplay;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Plugin runtime finish task
 */
public class PluginRuntimeFinishTask extends Task {

    private static final String TASK_NAME = "Plugin runtime finish";

    public PluginRuntimeFinishTask(ClientEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ClientHomeDisplay((ClientEngine) this.loggingEngine)));
    }
}
