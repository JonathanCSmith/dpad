package jonathansmith.dpad.server.gui.startup;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;
import jonathansmith.dpad.common.gui.util.BlankToolbar;
import jonathansmith.dpad.common.gui.util.ProgressPanel;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Server startup GUI. Shows the progress of the startup tasks to the user
 */
public class ServerStartupDisplay extends ServerDisplay {

    private final DisplayPanel toolbar_panel = new BlankToolbar();

    private final ProgressPanel progress_panel = new ProgressPanel();

    public ServerStartupDisplay(ServerEngine engine) {
        super(engine);

        this.engine.getEventThread().addEventListener(this.progress_panel);
    }

    @Override
    public DisplayPanel getToolbarComponent() {
        return this.toolbar_panel;
    }

    @Override
    public DisplayPanel getDisplayComponent() {
        return this.progress_panel;
    }

    @Override
    public void update() {
        this.progress_panel.update();
    }

    @Override
    public void onDestroy(IEngine loggingEngine) {
        loggingEngine.getEventThread().removeListener(this.progress_panel);
    }
}
