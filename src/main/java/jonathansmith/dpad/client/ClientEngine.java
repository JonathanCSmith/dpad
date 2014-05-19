package jonathansmith.dpad.client;

import java.net.SocketAddress;

import org.apache.log4j.Level;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.io.FileSystem;
import jonathansmith.dpad.common.engine.util.log.LoggerFactory;
import jonathansmith.dpad.common.engine.util.log.LoggingLevel;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.client.engine.executor.startup.ClientStartupExecutor;
import jonathansmith.dpad.client.gui.ClientTabController;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client engine. Implementation of a client side runtime engine, driving the actual program.
 */
public class ClientEngine extends Engine {

    public ClientEngine(SocketAddress address) {
        super(address, new ClientTabController());

        this.tabDisplay.setEngine(this);
        DPAD.getInstance().getGUI().addTab(this.tabDisplay);

        this.setFileSystem(new FileSystem(this));
        this.setLogger(LoggerFactory.getInstance().getLogger(this, new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO)));
    }

    @Override
    public void init() {
        super.init();
        if (this.hasErrored()) {
            return;
        }

        // Add the client startup executor as the first program to be run. Ensuring that everything is setup before anything else is performed.
        this.setProposedExecutor(new ClientStartupExecutor(this, this.address));
    }

    @Override
    public Platform getPlatform() {
        return Platform.CLIENT;
    }
}
