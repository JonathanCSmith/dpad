package jonathansmith.jdpad.server;

import java.net.SocketAddress;

import org.apache.log4j.Level;

import jonathansmith.jdpad.common.database.DatabaseManager;
import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.engine.io.FileSystem;
import jonathansmith.jdpad.common.engine.util.log.LoggerFactory;
import jonathansmith.jdpad.common.engine.util.log.LoggingLevel;
import jonathansmith.jdpad.common.platform.Platform;

import jonathansmith.jdpad.server.engine.executor.ServerStartup;
import jonathansmith.jdpad.server.gui.ServerTabController;

import jonathansmith.jdpad.JDPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Server Engine
 */
public class ServerEngine extends Engine {

    private boolean isSetup = false;

    private DatabaseManager databaseManager;

    public ServerEngine(SocketAddress address) {
        super(address, new ServerTabController());

        this.tabDisplay.setEngine(this);
        JDPAD.getInstance().getGUI().addTab(this.tabDisplay);

        this.setFileSystem(new FileSystem(this));
        this.setLogger(LoggerFactory.getInstance().getLogger(this, new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO)));
    }

    public void setDatabaseManager(DatabaseManager dbm) {
        if (dbm == null || this.databaseManager != null) {
            return;
        }

        this.databaseManager = dbm;
    }

    public boolean isSetup() {
        return this.isSetup;
    }

    public void setServerFinishedSetup() {
        this.isSetup = true;
    }

    @Override
    public void init() {
        super.init();
        if (this.hasErrored()) {
            return;
        }

        this.setCurrentExecutor(new ServerStartup(this, this.address));
    }

    @Override
    public void saveAndShutdown() {
        if (this.databaseManager != null) {
            this.databaseManager.shutdown(false);
        }

        super.saveAndShutdown();
    }

    @Override
    public void forceShutdown() {
        if (this.databaseManager != null) {
            this.databaseManager.shutdown(true);
        }

        super.forceShutdown();
    }

    @Override
    public Platform getPlatform() {
        return Platform.SERVER;
    }
}
