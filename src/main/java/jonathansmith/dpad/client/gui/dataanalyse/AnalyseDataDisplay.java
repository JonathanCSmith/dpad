package jonathansmith.dpad.client.gui.dataanalyse;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 18/09/2014.
 * <p/>
 * Load data display
 */
public class AnalyseDataDisplay extends ClientDisplay {

    private final AnalyseDataToolbar toolbar_panel;
    private final AnalyseDataPanel   display_panel;

    public AnalyseDataDisplay(ClientEngine engine) {
        super(engine);

        this.toolbar_panel = new AnalyseDataToolbar(engine);
        this.display_panel = new AnalyseDataPanel(engine, this);
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
    }

    @Override
    public void update() {

    }

    @Override
    public void onDestroy() {
        this.engine.getEventThread().removeListener(this.display_panel);
    }
}
