package jonathansmith.dpad.client.engine.event;

import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 18/09/2014.
 * <p/>
 * Event generated when the load data toolbar is interacted with
 */
public class LoadDataToolbarEvent extends Event {

    private final ToolbarStatus buttonPress;

    public LoadDataToolbarEvent(ToolbarStatus buttonPress) {
        this.buttonPress = buttonPress;
    }

    public ToolbarStatus getButtonPress() {
        return this.buttonPress;
    }

    public enum ToolbarStatus {
        BACK, REFRESH_PLUGINS
    }
}
