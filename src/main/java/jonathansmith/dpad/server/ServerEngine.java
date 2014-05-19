package jonathansmith.dpad.server;

import java.net.SocketAddress;

import jonathansmith.dpad.common.database.DatabaseManager;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.server.engine.executor.idle.ServerIdleExecutor;
import jonathansmith.dpad.server.engine.executor.startup.ServerStartupExecutor;
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

        // Add the server startup executor as the first program to be run. Ensuring that everything is setup before anything else is performed.
        this.setProposedExecutor(new ServerStartupExecutor(this, this.address));
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

    @Override
    protected Executor getDefaultExecutor() {
        return new ServerIdleExecutor(this);
    }
}
