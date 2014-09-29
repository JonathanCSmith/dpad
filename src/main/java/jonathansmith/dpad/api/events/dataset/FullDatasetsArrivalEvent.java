package jonathansmith.dpad.api.events.dataset;

import java.util.HashSet;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 29/09/2014.
 */
public class FullDatasetsArrivalEvent extends Event {

    private HashSet<DatasetRecord> datasets;

    public FullDatasetsArrivalEvent(HashSet<DatasetRecord> datasets) {
        this.datasets = datasets;
    }

    public HashSet<DatasetRecord> getDatasets() {
        return this.datasets;
    }
}
