package jonathansmith.dpad.client.gui.home;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.common.gui.util.BlankToolbar;
import jonathansmith.dpad.common.gui.util.ProgressPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Standard display for awaiting a response from the server. Utilises the generic progressbar display panel
 */
public class ProgressbarDisplay extends ClientDisplay {

    private final BlankToolbar  toolbar_panel = new BlankToolbar();
    private final ProgressPanel display_panel = new ProgressPanel();

    public ProgressbarDisplay(ClientEngine engine) {
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
    public void onActivation() {
        this.engine.getEventThread().addEventListener(this.display_panel);
    }

    @Override
    public void update() {
        this.display_panel.update();
    }

    @Override
    public void onDestroy() {
        this.engine.getEventThread().removeListener(this.display_panel);
    }
}
