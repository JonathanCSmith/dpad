package jonathansmith.dpad.api.events.dataset;

import java.util.HashSet;

import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.plugins.events.Event;

import jonathansmith.dpad.common.engine.state.ExperimentAdministrationState;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Event notifying the engine when the server has responded to experiment requests
 */
public abstract class ExperimentArrivalEvent extends Event {

    private final ExperimentAdministrationState responseType;

    public ExperimentArrivalEvent(ExperimentAdministrationState state) {
        this.responseType = state;
    }

    public ExperimentAdministrationState getResponseType() {
        return responseType;
    }

    public static class StateResponse extends ExperimentArrivalEvent {
        public StateResponse(ExperimentAdministrationState state) {
            super(state);
        }
    }

    public static class ExperimentResponse extends ExperimentArrivalEvent {

        private final ExperimentRecord record;

        public ExperimentResponse(ExperimentAdministrationState state, ExperimentRecord record) {
            super(state);

            this.record = record;
        }

        public ExperimentRecord getExperiment() {
            return this.record;
        }
    }

    public static class ExperimentRecordsResponse extends ExperimentArrivalEvent {

        private final HashSet<ExperimentRecord> experimentRecords;

        public ExperimentRecordsResponse(HashSet<ExperimentRecord> experiments) {
            super(ExperimentAdministrationState.SENDING_EXPERIMENTS);

            this.experimentRecords = experiments;
        }

        public HashSet<ExperimentRecord> getExperimentRecords() {
            return this.experimentRecords;
        }
    }
}
