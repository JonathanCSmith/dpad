package jonathansmith.dpad.client.gui.startup;

import java.awt.*;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;
import jonathansmith.dpad.common.gui.util.BlankPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Client startup properties
 */
public class ClientStartupPropertiesDisplay extends ClientDisplay {

    private final DisplayPanel toolbar_panel = new BlankPanel();
    private final ClientStartupPropertiesPanel config_panel;

    public ClientStartupPropertiesDisplay(ClientEngine loggingEngine) {
        super(loggingEngine);

        this.config_panel = new ClientStartupPropertiesPanel(engine);
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
