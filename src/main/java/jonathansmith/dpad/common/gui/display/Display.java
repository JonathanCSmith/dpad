package jonathansmith.dpad.common.gui.display;

import javax.swing.*;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Common parent for all possible displays. Allows for common formatting between display types across the program.
 */
public abstract class Display {

    public abstract JPanel getToolbarComponent();

    public abstract JPanel getDisplayComponent();
}
