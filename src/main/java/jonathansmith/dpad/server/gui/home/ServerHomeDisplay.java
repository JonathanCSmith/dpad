package jonathansmith.dpad.server.gui.home;

import java.awt.*;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Represents the basic GUI for the server.
 */
public class ServerHomeDisplay extends ServerDisplay {

    private final DisplayPanel    toolbar_panel = new ServerHomeToolbar();
    private final ServerHomePanel home_panel    = new ServerHomePanel();

    public ServerHomeDisplay(ServerEngine engine) {
        super(engine);

        // TODO: add when there are events to listen to!
        //this.engine.getEventThread().addEventListener(this.home_panel);
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
    public void update() {
        this.toolbar_panel.update();
        this.home_panel.update();
    }

    @Override
    public void onDestroy(IEngine loggingEngine) {
        loggingEngine.getEventThread().removeListener(this.home_panel);
    }
}
