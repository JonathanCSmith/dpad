package jonathansmith.jdpad.client.engine.executor;

import java.net.SocketAddress;

import org.apache.log4j.Level;

import jonathansmith.jdpad.common.engine.executor.Executor;
import jonathansmith.jdpad.common.engine.util.log.LoggerFactory;
import jonathansmith.jdpad.common.engine.util.log.LoggingLevel;
import jonathansmith.jdpad.common.platform.Platform;

import jonathansmith.jdpad.client.ClientEngine;
import jonathansmith.jdpad.client.network.ClientNetworkManager;

import jonathansmith.jdpad.JDPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client startup operation. Primarily involves netcode setup
 */
public class ClientStartup extends Executor {

    private final SocketAddress address;

    public ClientStartup(ClientEngine engine, SocketAddress address) {
        super(engine);

        this.address = address;
    }

    @Override
    public void execute() {
        this.engine.info("Beginning netwok initialisation", null);

        // Bind the net logger to our loggers
        LoggerFactory.getInstance().getLogger(this.engine, "io.netty", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));

        // Client Network Manager
        ClientNetworkManager cNM = new ClientNetworkManager(this.engine, this.address, JDPAD.getInstance().getPlatformSelection() == Platform.LOCAL);

        try {
            cNM.buildBootstap();
        }

        catch (Exception ex) {
            this.engine.handleError("Could not build bootstrap during network initialisation.", ex, true);
            return;
        }
        // TODO: cNM.start();
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
