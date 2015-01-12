package jonathansmith.dpad.client.engine.executor.idle;

import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.engine.executor.IdleTask;

import jonathansmith.dpad.client.ClientEngine;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Client Idle executor. Essentially sleeps and can be replaced for dedicated CPU tasks
 */
public class ClientIdleExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Client Idle";

    public ClientIdleExecutor(ClientEngine clientEngine) {
        super(EXECUTOR_NAME, clientEngine, true);

        this.addTask(new IdleTask(clientEngine));
    }
}
