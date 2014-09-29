package jonathansmith.dpad.common.gui.display;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Common parent for all possible displays. Allows for common formatting between display types across the program.
 */
public abstract class Display {

    public abstract DisplayPanel getToolbarComponent();

    public abstract DisplayPanel getDisplayComponent();

    public abstract void onActivation();

    public abstract void update();

    public abstract void onDestroy();
}
