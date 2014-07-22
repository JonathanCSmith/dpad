package jonathansmith.dpad.common.database.record;

import java.util.LinkedList;
import java.util.List;

import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecord;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Types of database record.
 */
public enum DatabaseRecord {

    SERVER_CONFIGURATION(ServerConfigurationRecord.class),
    LOADING_PLUGIN(LoadingPluginRecord.class),
    ANALYSING_PLUGIN(AnalysingPluginRecord.class);

    private final Class<? extends Record> record_parent;

    private DatabaseRecord(Class<? extends Record> recordClass) {
        this.record_parent = recordClass;
    }

    public static DatabaseRecord getRecordTypeFromClass(Class<? extends Record> clazz) {
        for (DatabaseRecord type : DatabaseRecord.values()) {
            if (type.getRecordClass().equals(clazz)) {
                return type;
            }
        }

        return null;
    }

    public static List<Class<? extends Record>> getRecordClasses() {
        LinkedList<Class<? extends Record>> classes = new LinkedList<Class<? extends Record>>();
        for (DatabaseRecord type : DatabaseRecord.values()) {
            classes.add(type.getRecordClass());
        }

        return classes;
    }

    public Class<? extends Record> getRecordClass() {
        return this.record_parent;
    }
}
