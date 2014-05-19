package jonathansmith.dpad.server.engine.executor;

import java.net.SocketAddress;

import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.server.ServerEngine;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Server startup executable
 */
public class ServerStartupExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Server Startup";

    public ServerStartupExecutor(ServerEngine engine, SocketAddress address) {
        super(EXECUTOR_NAME, engine, false);

        this.addTask(new SetupServerLoggingTask(engine));
        this.addTask(new SetupHibernateTask(engine));
        this.addTask(new SetupServerNetworkTask(engine, address, DPAD.getInstance().getPlatformSelection() == Platform.LOCAL));
        this.addTask(new FinishServerSetup(engine));
        // TODO: PluginManager setup task
        // TODO: Assess whether event thread should be constructed here
        // TODO: Assess whether file system should be constructed here
    }
}
