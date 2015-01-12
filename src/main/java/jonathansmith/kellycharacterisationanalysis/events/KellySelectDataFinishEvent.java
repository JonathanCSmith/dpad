package jonathansmith.kellycharacterisationanalysis.events;

import java.util.HashSet;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 29/09/2014.
 */
public class KellySelectDataFinishEvent extends Event {

    private final HashSet<DatasetRecord> interestedRecords;

    public KellySelectDataFinishEvent(HashSet<DatasetRecord> interestedRecords) {
        this.interestedRecords = interestedRecords;
    }

    public HashSet<DatasetRecord> getInterestedRecords() {
        return this.interestedRecords;
    }
}
