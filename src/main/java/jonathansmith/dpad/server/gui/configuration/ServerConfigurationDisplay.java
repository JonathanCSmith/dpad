package jonathansmith.dpad.server.gui.configuration;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.common.gui.util.BlankToolbar;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 17/07/2014.
 * <p/>
 * Displays the server configuration properties.
 */
public class ServerConfigurationDisplay extends ServerDisplay {

    private final DisplayPanel toolbar_panel = new BlankToolbar();

    private final ServerConfigurationPanel home_panel;

    public ServerConfigurationDisplay(ServerEngine engine) {
        super(engine);

        this.home_panel = new ServerConfigurationPanel(engine);
    }

    @Override
    public DisplayPanel getToolbarComponent() {
        return this.toolbar_panel;
    }

    @Override
    public DisplayPanel getDisplayComponent() {
        return this.home_panel;
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
