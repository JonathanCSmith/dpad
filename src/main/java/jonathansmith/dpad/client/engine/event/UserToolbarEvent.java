package jonathansmith.dpad.client.engine.event;

import jonathansmith.dpad.common.engine.event.Event;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Event triggered when a user either submits his/her information or logs out.
 */
public class UserToolbarEvent extends Event {

    private final byte buttonPress;

    public UserToolbarEvent(byte isAdministrate) {
        this.buttonPress = isAdministrate;
    }

    // Returns 0 for login/logout, 1 for switch to administration, 2 for back
    public byte getButtonPress() {
        return this.buttonPress;
    }
}
