package jonathansmith.dpad.server.database.record.sample;

import jonathansmith.dpad.api.database.SampleRecord;

import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 29/09/2014.
 */
public class SampleRecordManager extends RecordManager<SampleRecord> {

    private static SampleRecordManager instance;

    private SampleRecordManager() {
        super(new SampleRecordDAO(), SampleRecord.class);
    }

    public static SampleRecordManager getInstance() {
        if (instance == null) {
            instance = new SampleRecordManager();
        }

        return instance;
    }

    @Override
    public SampleRecordDAO getDAO() {
        return (SampleRecordDAO) this.database_access_object;
    }
}
