package jonathansmith.dpad.client.engine.executor.startup;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.startup.ClientStartupDisplay;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Sets up the client setup gui for client feedback of setup progress.
 */
public class ClientStartupGUISetupTask extends Task {

    private static final String TASK_NAME = "Client startup gui setup";

    public ClientStartupGUISetupTask(ClientEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    public void runTask() {
        ClientStartupDisplay display = new ClientStartupDisplay();
        display.init(this.loggingEngine);
        ClientDisplayChangeEvent event = new ClientDisplayChangeEvent(display);
        this.loggingEngine.getEventThread().postEvent(event);
    }
}
