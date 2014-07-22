package jonathansmith.dpad.server.database.record.loadingplugin;

import jonathansmith.dpad.common.database.record.LoadingPluginRecord;

import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Record manager for loading plugin records.
 */
public class LoadingPluginRecordManager extends RecordManager<LoadingPluginRecord> {

    private static LoadingPluginRecordManager instance;

    private LoadingPluginRecordManager() {
        super(new LoadingPluginRecordDAO(), LoadingPluginRecord.class);
    }

    public static LoadingPluginRecordManager getInstance() {
        if (instance == null) {
            instance = new LoadingPluginRecordManager();
        }

        return instance;
    }

    @Override
    public LoadingPluginRecordDAO getDAO() {
        return (LoadingPluginRecordDAO) this.database_access_object;
    }
}
