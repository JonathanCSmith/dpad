package jonathansmith.dpad.server.gui.configuration;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;
import jonathansmith.dpad.common.gui.util.BlankPanel;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 17/07/2014.
 * <p/>
 * Display's the server configuration properties.
 */
public class ServerConfigurationDisplay extends ServerDisplay {

    private final DisplayPanel toolbar_panel = new BlankPanel();
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
    public void update() {

    }

    @Override
    public void onDestroy(IEngine loggingEngine) {

    }
}
