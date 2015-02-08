package jonathansmith.dpad.server;

import java.net.SocketAddress;
import java.util.UUID;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.server.database.DatabaseManager;
import jonathansmith.dpad.server.database.ServerDatabaseConnection;
import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecord;
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

    private final ServerIdleExecutor executor;

    private boolean isDatabaseSetup = false;

    private DatabaseManager          databaseManager;
    private UUID                     serverUUID;
    private ServerDatabaseConnection serverDatabaseConnection;

    public ServerEngine(SocketAddress address) {
        super(address, new ServerTabController());

        this.engine_tab_controller.setEngine(this);
        DPAD.getInstance().getGUI().addTab(this.engine_tab_controller);

        // Add the server startup executor as the first program to be run. Ensuring that everything is setup before anything else is performed.
        this.setProposedExecutor(new ServerStartupExecutor(this, this.address));
        this.executor = new ServerIdleExecutor(this);
    }

    public UUID getServerUUID() {
        return this.serverUUID;
    }

    public void setServerUUID(UUID serverUUID) {
        this.serverUUID = serverUUID;
    }

    public void setDatabaseManager(DatabaseManager dbm) {
        if (dbm == null || this.isDatabaseSetup) {
            return;
        }

        this.databaseManager = dbm;
        this.databaseManager.buildServerConnection(this);
        this.isDatabaseSetup = true;
    }

    public void assignServerConnection(ServerDatabaseConnection connection) {
        this.serverDatabaseConnection = connection;
    }

    public ServerConfigurationRecord getServerConfiguration() {
        return this.serverDatabaseConnection.loadConfiguration();
    }

    public void saveServerConfiguration(ServerConfigurationRecord configuration) {
        this.serverDatabaseConnection.saveConfiguration(configuration);
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
        return this.executor;
    }
}
