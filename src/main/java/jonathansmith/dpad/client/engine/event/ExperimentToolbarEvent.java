package jonathansmith.dpad.client.engine.event;

import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Event triggered by experiment administration
 */
public class ExperimentToolbarEvent extends Event {

    private final ToolbarStatus buttonPress;

    public ExperimentToolbarEvent(ToolbarStatus buttonPress) {
        this.buttonPress = buttonPress;
    }

    public ToolbarStatus getButtonPress() {
        return this.buttonPress;
    }

    public enum ToolbarStatus {
        CREATE_EXPERIMENT,
        LOAD_EXPERIMENT,
        BACK,
        RESET
    }
}
