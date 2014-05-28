package jonathansmith.dpad.server.database.record.analysingplugin;

import jonathansmith.dpad.common.database.record.AnalysingPluginRecord;
import jonathansmith.dpad.common.database.record.DatabaseRecord;

import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Record manager for analysing plugin records
 */
public class AnalysingPluginRecordManager extends RecordManager<AnalysingPluginRecord> {

    private static AnalysingPluginRecordManager instance;

    private AnalysingPluginRecordManager() {
        super(new AnalysingPluginRecordDAO(), AnalysingPluginRecord.class);
    }

    public static AnalysingPluginRecordManager getInstance() {
        if (instance == null) {
            instance = new AnalysingPluginRecordManager();
        }

        return instance;
    }

    @Override
    public AnalysingPluginRecordDAO getDAO() {
        return (AnalysingPluginRecordDAO) this.database_access_object;
    }

    @Override
    public AnalysingPluginRecord loadChildrenForUpdate(AnalysingPluginRecord record, DatabaseRecord childType) {
        // TODO:
        return null;
    }
}
