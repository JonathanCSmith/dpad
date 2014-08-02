package jonathansmith.dpad.client.engine.executor;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.home.ClientHomeDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Generic rebuild home task for returning to the core client display.
 */
public class RebuildClientHomeTask extends Task {

    private static final String TASK_NAME = "Rebuild Client Home";

    public RebuildClientHomeTask(ClientEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ClientHomeDisplay((ClientEngine) this.loggingEngine)));
    }
}
