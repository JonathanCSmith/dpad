package jonathansmith.dpad.server.engine.executor;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.server.ServerEngine;

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
        // Notify that executor has finished
        this.loggingEngine.trace("Server setup complete", null);
        this.engine.setServerFinishedSetup();
    }
}
