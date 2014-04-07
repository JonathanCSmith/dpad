package jonathansmith.dpad.client.engine.executor;

import java.net.SocketAddress;

import org.apache.log4j.Level;

import jonathansmith.dpad.DPAD;
import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.network.ClientNetworkManager;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.engine.util.log.LoggerFactory;
import jonathansmith.dpad.common.engine.util.log.LoggingLevel;
import jonathansmith.dpad.common.platform.Platform;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client startup operation. Primarily invokes setup of the network.
 */
public class ClientStartupExecutor extends Executor {

    private final SocketAddress address;

    public ClientStartupExecutor(ClientEngine engine, SocketAddress address) {
        super(engine);

        this.address = address;
    }

    @Override
    public void execute() {
        this.engine.info("Beginning netwok initialisation", null);

        // Bind the net logger to our loggers
        LoggerFactory.getInstance().getLogger(this.engine, "io.netty", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));

        // Client Network Manager
        ClientNetworkManager cNM = new ClientNetworkManager(this.engine, this.address, DPAD.getInstance().getPlatformSelection() == Platform.LOCAL);

        try {
            cNM.buildBootstap();
        }

        catch (Exception ex) {
            this.engine.handleError("Could not connect to provided network during network initialisation.", ex, true);
            return;
        }

        cNM.start();
        this.engine.info("Network initialisation complete", null);

        // Hand off information to engine
        this.engine.setNetworkManager(cNM);

        // Notify that executor has finished
        this.setFinished();
    }

    @Override
    public void shutdown(boolean forceShutdownFlag) {
    }
}
