package jonathansmith.dpad.client.gui.experiment;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Experiment Display
 */
public class ExperimentDisplay extends ClientDisplay {

    private final ExperimentAdministrationToolbar toolbar_panel;
    private final ExperimentAdministrationPanel   display_panel;

    public ExperimentDisplay(ClientEngine engine) {
        super(engine);

        this.toolbar_panel = new ExperimentAdministrationToolbar(engine, this);
        this.display_panel = new ExperimentAdministrationPanel(engine, this);
    }

    @Override
    public DisplayPanel getToolbarComponent() {
        return this.toolbar_panel;
    }

    @Override
    public DisplayPanel getDisplayComponent() {
        return this.display_panel;
    }

    @Override
    public void onActivation() {
        this.engine.getEventThread().addEventListener(this.display_panel);
        this.engine.getEventThread().addEventListener(this.toolbar_panel);
    }

    @Override
    public void update() {
        this.toolbar_panel.update();
        this.display_panel.update();
    }

    @Override
    public void onDestroy() {
        this.engine.getEventThread().removeListener(this.display_panel);
        this.engine.getEventThread().removeListener(this.toolbar_panel);
    }
}
