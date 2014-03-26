package jonathansmith.jdpad.server.gui;

import java.awt.*;

import jonathansmith.jdpad.common.gui.display.Display;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Server display. Contains all methods for correct display on the server side tab
 */
public abstract class ServerDisplay extends Display {

    public Component getToolbarComponent() {
        return null;
    }

    public Component getDisplayComponent() {
        return null;
    }
}
