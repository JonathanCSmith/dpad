package jonathansmith.jdpad.server.engine.event;

import jonathansmith.jdpad.common.engine.event.DisplayChangeEvent;

import jonathansmith.jdpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Event fired when the server should change it's display
 */
public class ServerDisplayChangeEvent extends DisplayChangeEvent {

    public ServerDisplayChangeEvent(ServerDisplay display) {
        super(display);
    }
}
