package jonathansmith.dpad.server.database;

import jonathansmith.dpad.common.database.util.RecordList;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecord;
import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecordManager;

/**
 * Created by Jon on 17/07/2014.
 * <p/>
 * Server side database connection. Allows *only* the server to access the database. For internal purposes only.
 */
public class ServerDatabaseConnection {

    private final ServerEngine       engine;
    private final DatabaseConnection serverDatabaseConnection;

    public ServerDatabaseConnection(ServerEngine engine, DatabaseConnection connectionFromUUID) {
        this.engine = engine;
        this.serverDatabaseConnection = connectionFromUUID;
    }

    public void saveConfiguration(ServerConfigurationRecord configuration) {
        RecordList<ServerConfigurationRecord> configurations = ServerConfigurationRecordManager.getInstance().loadAll(this.serverDatabaseConnection);
        if (configurations.size() != 1 || !configurations.get(0).getUUID().contentEquals(configuration.getUUID())) {
            for (ServerConfigurationRecord config : configurations) {
                ServerConfigurationRecordManager.getInstance().deleteExisting(this.serverDatabaseConnection, config);
            }
        }

        ServerConfigurationRecordManager.getInstance().save(this.serverDatabaseConnection, configuration);
    }

    public ServerConfigurationRecord loadConfiguration() {
        return ServerConfigurationRecordManager.getInstance().findByID(this.serverDatabaseConnection, this.engine.getServerUUID().toString());
    }
}
