package jonathansmith.dpad.client.engine.event;

import jonathansmith.dpad.common.engine.event.Event;
import jonathansmith.dpad.common.engine.user.UserResponseState;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Event that occurs when the server has replied to some form of user administration
 */
public class ServerUserResponseEvent extends Event {

    private final UserResponseState state;

    public ServerUserResponseEvent(UserResponseState state) {
        this.state = state;
    }

    public UserResponseState getState() {
        return this.state;
    }
}
