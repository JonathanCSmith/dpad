package jonathansmith.dpad.server;

import java.net.SocketAddress;

import org.apache.log4j.Level;

import jonathansmith.dpad.common.database.DatabaseManager;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.io.FileSystem;
import jonathansmith.dpad.common.engine.util.log.LoggerFactory;
import jonathansmith.dpad.common.engine.util.log.LoggingLevel;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.server.engine.executor.ServerStartup;
import jonathansmith.dpad.server.gui.ServerTabController;

import jonathansmith.dpad.DPAD;

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
        DPAD.getInstance().getGUI().addTab(this.tabDisplay);

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
