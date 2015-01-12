package jonathansmith.dpad.client.gui.plugin;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.common.gui.util.BlankToolbar;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Temporary display for plugins while they are setting up
 */
public class PluginTemporaryDisplay extends ClientDisplay {

    private final PluginTemporaryDisplayPanel display_panel = new PluginTemporaryDisplayPanel();
    private final BlankToolbar                toolbar_panel = new BlankToolbar();

    public PluginTemporaryDisplay(ClientEngine engine) {
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
    }

    @Override
    public void update() {

    }

    @Override
    public void onDestroy() {

    }
}
