package jonathansmith.dpad.client.engine.event;

import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Event triggered when a user either submits his/her information or logs out.
 * <p/>
 * Values for the button press field are:
 * 0 - login/logout
 * 1 - switch administration type
 * 2 - back
 */
public class UserToolbarEvent extends Event {

    private final ToolbarStatus buttonPress;

    public UserToolbarEvent(ToolbarStatus buttonPress) {
        this.buttonPress = buttonPress;
    }

    // Returns 0 for login/logout, 1 for switch to administration, 2 for back
    public ToolbarStatus getButtonPress() {
        return this.buttonPress;
    }

    public enum ToolbarStatus {
        LOGOUT, LOGIN, BACK, CHANGE_PASSWORD, NEW_USER
    }
}
