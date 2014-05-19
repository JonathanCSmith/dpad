package jonathansmith.dpad.client.engine.executor.startup;

import java.net.SocketAddress;

import jonathansmith.dpad.common.engine.executor.CommonSetupTask;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.client.ClientEngine;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client startup operation. Primarily invokes setup of the network.
 */
public class ClientStartupExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Client Startup";

    public ClientStartupExecutor(ClientEngine engine, SocketAddress address) {
        super(EXECUTOR_NAME, engine, false);

        this.addTask(new CommonSetupTask(engine));
        this.addTask(new SetupClientLoggingTask(engine));
        this.addTask(new SetupClientNetworkTask(engine, address, DPAD.getInstance().getPlatformSelection() == Platform.LOCAL));
    }
}
