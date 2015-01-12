package jonathansmith.dpad.server.gui;

import jonathansmith.dpad.common.gui.display.Display;

import jonathansmith.dpad.server.ServerEngine;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Generic parent for all server gui display types. Allows for a common display type across all server displays
 */
public abstract class ServerDisplay extends Display {

    protected final ServerEngine engine;

    public ServerDisplay(ServerEngine engine) {
        this.engine = engine;
    }
}
