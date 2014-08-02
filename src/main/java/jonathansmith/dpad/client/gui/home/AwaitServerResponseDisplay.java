package jonathansmith.dpad.client.gui.home;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;
import jonathansmith.dpad.common.gui.util.BlankToolbar;
import jonathansmith.dpad.common.gui.util.ProgressPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Standard display for awaiting a response from the server. Utilises the generic progressbar display panel
 */
public class AwaitServerResponseDisplay extends ClientDisplay {

    private final DisplayPanel toolbar_panel = new BlankToolbar();
    private final DisplayPanel display_panel = new ProgressPanel();

    public AwaitServerResponseDisplay(ClientEngine engine) {
        super(engine);
    }

    @Override
    public DisplayPanel getToolbarComponent() {
        return this.toolbar_panel;
    }

    @Override
    public DisplayPanel getDisplayComponent() {
        return this.display_panel;
    }

    @Override
    public void update() {

    }

    @Override
    public void onDestroy(IEngine loggingEngine) {

    }
}
