package jonathansmith.dpad.server.engine.executor.idle;

import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.engine.executor.IdleTask;

import jonathansmith.dpad.server.ServerEngine;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Server Idle executor. Essentially is an event listener to determine which executors to queue up.
 */
public class ServerIdleExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Server Idle";

    public ServerIdleExecutor(ServerEngine serverEngine) {
        super(EXECUTOR_NAME, serverEngine, true);

        // TODO: Add idle tasks
        this.addTask(new IdleTask(serverEngine));
    }
}
