package jonathansmith.dpad.server.database.record.measurement;

import jonathansmith.dpad.api.database.MeasurementConditionRecord;

import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 29/09/2014.
 */
public class MeasurementRecordManager extends RecordManager<MeasurementConditionRecord> {

    private static MeasurementRecordManager instance;

    private MeasurementRecordManager() {
        super(new MeasurementRecordDAO(), MeasurementConditionRecord.class);
    }

    public static MeasurementRecordManager getInstance() {
        if (instance == null) {
            instance = new MeasurementRecordManager();
        }

        return instance;
    }

    @Override
    public MeasurementRecordDAO getDAO() {
        return (MeasurementRecordDAO) this.database_access_object;
    }
}
