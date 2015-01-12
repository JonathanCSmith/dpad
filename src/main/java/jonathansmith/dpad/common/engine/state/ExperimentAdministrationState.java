package jonathansmith.dpad.common.engine.state;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Experiment administration state flags
 */
public enum ExperimentAdministrationState {
    NOT_LOGGED_IN,

    NEW_EXPERIMENT,
    EXPERIMENT_NAME_NOT_UNIQUE,
    EXPERIMENT_CREATION_SUCCESS,

    SETTING_CURRENT_EXPERIMENT,
    CANNOT_FIND_PROVIDED_EXPERIMENT,
    EXPERIMENT_SELECTION_SUCCESS,

    REQUESTING_EXPERIMENTS,
    SENDING_EXPERIMENTS
}
