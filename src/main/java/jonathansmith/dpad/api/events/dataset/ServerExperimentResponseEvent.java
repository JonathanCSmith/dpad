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
public abstract class ServerExperimentResponseEvent extends Event {

    private final ExperimentAdministrationState responseType;

    public ServerExperimentResponseEvent(ExperimentAdministrationState state) {
        this.responseType = state;
    }

    public ExperimentAdministrationState getResponseType() {
        return responseType;
    }

    public static class StateResponse extends ServerExperimentResponseEvent {
        public StateResponse(ExperimentAdministrationState state) {
            super(state);
        }
    }

    public static class ExperimentResponse extends ServerExperimentResponseEvent {

        private final ExperimentRecord record;

        public ExperimentResponse(ExperimentAdministrationState state, ExperimentRecord record) {
            super(state);

            this.record = record;
        }

        public ExperimentRecord getExperiment() {
            return this.record;
        }
    }

    public static class ExperimentRecordsResponse extends ServerExperimentResponseEvent {

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
