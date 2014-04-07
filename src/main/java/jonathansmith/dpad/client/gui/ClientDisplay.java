package jonathansmith.dpad.client.gui;

import javax.swing.*;

import jonathansmith.dpad.common.gui.display.Display;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Generic parent for all client gui display types
 */
public abstract class ClientDisplay extends Display {

    public abstract JPanel getToolbarComponent();

    public abstract JPanel getDisplayComponent();
}
