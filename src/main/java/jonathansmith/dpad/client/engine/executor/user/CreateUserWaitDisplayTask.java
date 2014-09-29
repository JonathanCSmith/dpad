package jonathansmith.dpad.client.engine.executor.user;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.home.ProgressbarDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Create the progressbar for awaiting server response for users
 */
public class CreateUserWaitDisplayTask extends Task {

    private static final String TASK_NAME = "Create User Wait Display";

    public CreateUserWaitDisplayTask(IEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ProgressbarDisplay((ClientEngine) this.loggingEngine)));
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Sending user information to server", 0, 2, 0));
    }
}
