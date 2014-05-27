package jonathansmith.dpad.server.engine.executor.startup;

import java.net.SocketAddress;

import jonathansmith.dpad.common.engine.executor.CommonSetupTask;
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

        this.addTask(new ServerStartupGUISetupTask(engine));
        this.addTask(new CommonSetupTask(engine));
        this.addTask(new SetupServerLoggingTask(engine));
        this.addTask(new SetupHibernateTask(engine));
        this.addTask(new SetupServerNetworkTask(engine, address, DPAD.getInstance().getPlatformSelection() == Platform.LOCAL));
        this.addTask(new FinishServerSetup(engine));
    }
}
