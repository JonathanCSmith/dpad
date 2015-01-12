package jonathansmith.dpad.client.gui;

import jonathansmith.dpad.common.gui.display.Display;

import jonathansmith.dpad.client.ClientEngine;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Generic parent for all client gui display types. Allows for a common display type across all client displays
 */
public abstract class ClientDisplay extends Display {

    protected final ClientEngine engine;

    public ClientDisplay(ClientEngine engine) {
        this.engine = engine;
    }
}
