package jonathansmith.dpad.client.gui.startup;

import java.awt.*;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.display.DisplayPanel;
import jonathansmith.dpad.common.gui.util.BlankPanel;
import jonathansmith.dpad.common.gui.util.ProgressPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Client startup display. Displays the startup progress of the client engine.
 */
public class ClientStartupDisplay extends ClientDisplay {

    private final DisplayPanel  toolbar_panel  = new BlankPanel();
    private final ProgressPanel progress_panel = new ProgressPanel();

    public ClientStartupDisplay(ClientEngine engine) {
        super(engine);

        this.engine.getEventThread().addEventListener(this.progress_panel);
        this.toolbar_panel.getContentPane().setMaximumSize(new Dimension(100, -1));
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
