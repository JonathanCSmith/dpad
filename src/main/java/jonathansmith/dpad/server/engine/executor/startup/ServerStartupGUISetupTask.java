package jonathansmith.dpad.server.engine.executor.startup;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.engine.event.ServerDisplayChangeEvent;
import jonathansmith.dpad.server.gui.startup.ServerStartupDisplay;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Server GUI setup task
 */
public class ServerStartupGUISetupTask extends Task {

    private static final String TASK_NAME = "Sever startup gui setup";

    public ServerStartupGUISetupTask(ServerEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    public void runTask() {
        ServerStartupDisplay display = new ServerStartupDisplay((ServerEngine) this.loggingEngine);
        ServerDisplayChangeEvent event = new ServerDisplayChangeEvent(display);
        this.loggingEngine.getEventThread().postEvent(event);
    }
}
