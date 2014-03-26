package jonathansmith.jdpad.client.engine.event;

import jonathansmith.jdpad.common.engine.event.DisplayChangeEvent;

import jonathansmith.jdpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Event posted when the client changes the display
 */
public class ClientDisplayChangeEvent extends DisplayChangeEvent {

    public ClientDisplayChangeEvent(ClientDisplay display) {
        super(display);
    }
}
