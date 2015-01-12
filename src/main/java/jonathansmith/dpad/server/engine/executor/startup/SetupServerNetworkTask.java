package jonathansmith.dpad.server.engine.executor.startup;

import java.net.SocketAddress;

import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;

import jonathansmith.dpad.common.engine.executor.Task;
import jonathansmith.dpad.common.network.NetworkManager;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.network.ServerNetworkManager;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Server network setup
 */
public class SetupServerNetworkTask extends Task {

    private static final String TASK_NAME = "Server Network Setup";

    private final ServerEngine  engine;
    private final SocketAddress address;
    private final boolean       isLocalConnection;

    public SetupServerNetworkTask(ServerEngine engine, SocketAddress address, boolean isLocal) {
        super(TASK_NAME, engine);

        this.engine = engine;
        this.address = address;
        this.isLocalConnection = isLocal;
    }

    @Override
    public void runTask() {
        // Server Network Manager
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 2, 0));
        this.loggingEngine.info("Beginning network initialisation", null);
        this.loggingEngine.trace("Creating server side network manager", null);
        NetworkManager sNM = new ServerNetworkManager(this.engine, this.address, this.isLocalConnection);

        try {
            this.loggingEngine.trace("Attempting to initialise network infrastructure", null);
            sNM.buildBootstrap();
            this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 2, 1));
        }

        catch (Exception ex) {
            this.loggingEngine.handleError("Could not build bootstrap during network initialisation.", ex);
            return;
        }

        this.loggingEngine.trace("Starting network manager thread", null);
        sNM.start();
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 2, 2));
        this.loggingEngine.info("Network initialisation complete", null);

        // Hand off information to engine
        this.engine.setNetworkManager(sNM);
    }
}
