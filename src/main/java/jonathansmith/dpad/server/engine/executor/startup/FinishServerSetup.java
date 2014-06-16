package jonathansmith.dpad.server.engine.executor.startup;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.engine.event.ServerDisplayChangeEvent;
import jonathansmith.dpad.server.gui.home.ServerHomeDisplay;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Final task for server setup. Allows correct ordering of setups when local connections are being created (i.e. server finishes first)
 */
public class FinishServerSetup extends Task {

    private static final String TASK_NAME = "Server Finish Setup";

    private final ServerEngine engine;

    public FinishServerSetup(ServerEngine engine) {
        super(TASK_NAME, engine);

        this.engine = engine;
    }

    @Override
    public void runTask() {
        this.loggingEngine.trace("Setting up server home display", null);
        ServerDisplayChangeEvent event = new ServerDisplayChangeEvent(new ServerHomeDisplay((ServerEngine) this.loggingEngine));
        this.loggingEngine.getEventThread().postEvent(event);

        // Notify that executor has finished
        this.loggingEngine.trace("Server setup complete", null);
        this.engine.setServerFinishedSetup();
    }
}
