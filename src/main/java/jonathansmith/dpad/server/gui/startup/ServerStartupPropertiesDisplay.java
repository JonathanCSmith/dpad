package jonathansmith.dpad.server.gui.startup;

import java.awt.*;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;
import jonathansmith.dpad.common.gui.util.BlankPanel;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 13/07/2014.
 * <p/>
 * Server Config Display.
 */
public class ServerStartupPropertiesDisplay extends ServerDisplay {

    private final DisplayPanel toolbar_panel = new BlankPanel();
    private final ServerStartupPropertiesPanel config_panel;

    public ServerStartupPropertiesDisplay(ServerEngine engine) {
        super(engine);

        this.config_panel = new ServerStartupPropertiesPanel(engine);
        this.toolbar_panel.getContentPane().setMaximumSize(new Dimension(100, -1));
    }

    @Override
    public DisplayPanel getToolbarComponent() {
        return this.toolbar_panel;
    }

    @Override
    public DisplayPanel getDisplayComponent() {
        return this.config_panel;
    }

    @Override
    public void update() {

    }

    @Override
    public void onDestroy(IEngine loggingEngine) {

    }
}
