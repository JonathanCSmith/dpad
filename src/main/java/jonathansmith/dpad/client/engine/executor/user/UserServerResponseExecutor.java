package jonathansmith.dpad.client.engine.executor.user;

import jonathansmith.dpad.common.engine.executor.Executor;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.executor.RebuildClientHomeTask;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Server response wait executor for user actions.
 */
public class UserServerResponseExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Wait for User Response";

    public UserServerResponseExecutor(ClientEngine engine) {
        super(EXECUTOR_NAME, engine, false);

        this.addTask(new CreateUserWaitDisplayTask(engine));
        this.addTask(new UserResponseWaitTask(engine));
        this.addTask(new RebuildClientHomeTask(engine));
    }
}
