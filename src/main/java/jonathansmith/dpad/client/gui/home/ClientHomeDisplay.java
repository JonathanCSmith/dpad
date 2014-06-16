package jonathansmith.dpad.client.gui.home;

import java.awt.*;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Base GUI for the client. Represents all functionality that is available on the client.
 * Abilities are determined by the user's credentials.
 */
public class ClientHomeDisplay extends ClientDisplay {

    private final ClientHomeToolbar toolbar_panel;
    private final ClientHomePanel   home_panel;

    public ClientHomeDisplay(ClientEngine engine) {
        super(engine);

        this.toolbar_panel = new ClientHomeToolbar(this.engine);
        this.toolbar_panel.getContentPane().setMaximumSize(new Dimension(100, -1));
        this.home_panel = new ClientHomePanel(this.engine);
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
        this.toolbar_panel.update();
        this.home_panel.update();
    }

    @Override
    public void onDestroy(IEngine loggingEngine) {

    }
}
