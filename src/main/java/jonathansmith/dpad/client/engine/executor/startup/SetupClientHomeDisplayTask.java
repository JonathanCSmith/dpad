package jonathansmith.dpad.client.engine.executor.startup;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.home.ClientHomeDisplay;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Creates the client home display.
 */
public class SetupClientHomeDisplayTask extends Task {

    private static final String TASK_NAME = "Client Home Display Setup";

    public SetupClientHomeDisplayTask(ClientEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    public void runTask() {
        this.loggingEngine.trace("Creating home display", null);
        ClientDisplayChangeEvent event = new ClientDisplayChangeEvent(new ClientHomeDisplay((ClientEngine) this.loggingEngine));
        this.loggingEngine.getEventThread().postEvent(event);
    }
}
