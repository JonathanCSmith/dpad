package jonathansmith.dpad.server.gui.startup;

import java.awt.*;

import javax.swing.*;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.util.ProgressPanel;

import jonathansmith.dpad.server.gui.ServerDisplay;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Server startup GUI. Shows the progress of the startup tasks to the user
 */
public class ServerStartupDisplay extends ServerDisplay {

    private final JPanel        toolbar_display  = new JPanel();
    private final ProgressPanel progress_display = new ProgressPanel();

    public ServerStartupDisplay() {
        this.toolbar_display.setMaximumSize(new Dimension(100, -1));
    }

    @Override
    public JPanel getToolbarComponent() {
        return this.toolbar_display;
    }

    @Override
    public JPanel getDisplayComponent() {
        return this.progress_display.getContentPane();
    }

    @Override
    public void init(IEngine loggingEngine) {
        loggingEngine.getEventThread().addEventListener(this.progress_display);
    }

    @Override
    public void update() {
        this.progress_display.update();
    }

    @Override
    public void onDestroy(IEngine loggingEngine) {
        loggingEngine.getEventThread().removeListener(this.progress_display);
    }
}
