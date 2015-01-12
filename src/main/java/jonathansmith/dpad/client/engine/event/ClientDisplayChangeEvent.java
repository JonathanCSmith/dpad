package jonathansmith.dpad.client.engine.event;

import jonathansmith.dpad.common.engine.event.gui.DisplayChangeEvent;

import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Event posted when the client changes the display
 */
public class ClientDisplayChangeEvent extends DisplayChangeEvent {

    public ClientDisplayChangeEvent(ClientDisplay display) {
        super(display);
    }

    public ClientDisplay getTargetDisplay() {
        return (ClientDisplay) this.targetDisplay;
    }
}
