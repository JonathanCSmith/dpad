package jonathansmith.dpad.client.engine.executor.startup;

import java.net.SocketAddress;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Task;
import jonathansmith.dpad.common.network.NetworkManager;

import jonathansmith.dpad.client.network.ClientNetworkManager;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Client Network setup task
 */
public class SetupClientNetworkTask extends Task {

    private static final String TASK_NAME = "Client Network Setup";

    private final Engine        engine;
    private final SocketAddress address;
    private final boolean       isLocalConnection;

    public SetupClientNetworkTask(Engine engine, SocketAddress address, boolean isLocal) {
        super(TASK_NAME, engine);

        this.engine = engine;
        this.address = address;
        this.isLocalConnection = isLocal;
    }

    @Override
    public void runTask() {
        this.loggingEngine.info("Beginning network initialisation", null);

        // Client Network Manager
        this.loggingEngine.trace("Building client network manager", null);
        NetworkManager cNM = new ClientNetworkManager(this.engine, this.address, this.isLocalConnection);

        try {
            this.loggingEngine.trace("Attempting to connect to server", null);
            cNM.buildBootstap();
        }

        // Generic exception as there are none specifically thrown. We catch, log, and shutdown the client.
        catch (Exception ex) {
            this.loggingEngine.handleError("Could not connect to provided network during network initialisation.", ex, true);
            return;
        }

        cNM.start();
        this.loggingEngine.info("Network initialisation complete", null);

        // Hand off information to engine
        this.engine.setNetworkManager(cNM);
    }
}
