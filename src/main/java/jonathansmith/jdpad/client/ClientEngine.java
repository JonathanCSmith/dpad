package jonathansmith.jdpad.client;

import java.net.SocketAddress;

import org.apache.log4j.Level;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.engine.io.FileSystem;
import jonathansmith.jdpad.common.engine.util.log.LoggerFactory;
import jonathansmith.jdpad.common.engine.util.log.LoggingLevel;
import jonathansmith.jdpad.common.platform.Platform;

import jonathansmith.jdpad.client.engine.executor.ClientStartup;
import jonathansmith.jdpad.client.gui.ClientTabController;

import jonathansmith.jdpad.JDPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client engine
 */
public class ClientEngine extends Engine {

    public ClientEngine(SocketAddress address) {
        super(address, new ClientTabController());

        this.tabDisplay.setEngine(this);
        JDPAD.getInstance().getGUI().addTab(this.tabDisplay);

        this.setFileSystem(new FileSystem(this));
        this.setLogger(LoggerFactory.getInstance().getLogger(this, new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO)));
    }

    @Override
    public void init() {
        super.init();
        if (this.hasErrored()) {
            return;
        }

        this.setCurrentExecutor(new ClientStartup(this, this.address));
    }

    @Override
    public Platform getPlatform() {
        return Platform.CLIENT;
    }
}
