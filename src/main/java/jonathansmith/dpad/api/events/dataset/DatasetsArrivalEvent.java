package jonathansmith.dpad.api.events.dataset;

import java.util.HashSet;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 29/09/2014.
 */
public class DatasetsArrivalEvent extends Event {

    private final HashSet<DatasetRecord> datasets;

    public DatasetsArrivalEvent(HashSet<DatasetRecord> interestedRecords) {
        this.datasets = interestedRecords;
    }

    public HashSet<DatasetRecord> getDatasets() {
        return this.datasets;
    }
}
