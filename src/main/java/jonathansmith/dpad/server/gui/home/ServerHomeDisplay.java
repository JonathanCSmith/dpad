package jonathansmith.dpad.server.gui.home;

import java.awt.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Represents the basic GUI for the server.
 */
public class ServerHomeDisplay extends ServerDisplay {

    private final ServerHomePanel home_panel = new ServerHomePanel();

    private final DisplayPanel toolbar_panel;

    public ServerHomeDisplay(ServerEngine engine) {
        super(engine);

        this.toolbar_panel = new ServerHomeToolbar(engine);
        this.toolbar_panel.getContentPane().setMaximumSize(new Dimension(100, -1));
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
        this.toolbar_panel.update();
        this.home_panel.update();
    }

    @Override
    public void onDestroy() {

    }
}
