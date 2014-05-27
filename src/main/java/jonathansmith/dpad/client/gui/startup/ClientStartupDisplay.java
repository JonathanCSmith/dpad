package jonathansmith.dpad.client.gui.startup;

import java.awt.*;

import javax.swing.*;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.gui.util.ProgressPanel;

import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Client startup display. Displays the startup progress of the client engine.
 */
public class ClientStartupDisplay extends ClientDisplay {

    private final JPanel        toolbar_display  = new JPanel();
    private final ProgressPanel progress_display = new ProgressPanel();

    public ClientStartupDisplay() {
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
