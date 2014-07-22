package jonathansmith.dpad.server.database.record.serverconfiguration;

import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 17/07/2014.
 * <p/>
 * Record manager for server configuration.
 */
public class ServerConfigurationRecordManager extends RecordManager<ServerConfigurationRecord> {

    private static ServerConfigurationRecordManager instance;

    public ServerConfigurationRecordManager() {
        super(new ServerConfigurationRecordDAO(), ServerConfigurationRecord.class);
    }

    public static ServerConfigurationRecordManager getInstance() {
        if (instance == null) {
            instance = new ServerConfigurationRecordManager();
        }

        return instance;
    }

    @Override
    public ServerConfigurationRecordDAO getDAO() {
        return (ServerConfigurationRecordDAO) this.database_access_object;
    }
}
