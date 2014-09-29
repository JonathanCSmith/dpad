package jonathansmith.kellycharacterisationanalysis.display.assignment;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;

/**
 * Created by Jon on 29/09/2014.
 */
public class DataAssignmentDisplay implements IPluginDisplay {

    private final KellyCharacterisationAnalysis core;
    private final IPluginRuntime                runtime;
    private final DataAssignmentToolbarPanel    toolbar_panel;
    private final DataAssignmentDisplayPanel    display_panel;

    public DataAssignmentDisplay(KellyCharacterisationAnalysis core, IPluginRuntime runtime) {
        this.core = core;
        this.runtime = runtime;

        this.toolbar_panel = new DataAssignmentToolbarPanel(this.core, this.runtime);
        this.display_panel = new DataAssignmentDisplayPanel(this.core, this.runtime);
    }

    @Override
    public DisplayPanel getDisplayToolbar() {
        return this.toolbar_panel;
    }

    @Override
    public DisplayPanel getDisplayPanel() {
        return this.display_panel;
    }

    @Override
    public void onDisplayActivation() {
        this.runtime.getEventThread().addEventListener(this.toolbar_panel);
        this.runtime.getEventThread().addEventListener(this.display_panel);
    }

    @Override
    public void onDisplayUpdate() {
        this.toolbar_panel.update();
        this.display_panel.update();
    }

    @Override
    public void onDisplayDestroy() {
        this.runtime.getEventThread().removeListener(this.toolbar_panel);
        this.runtime.getEventThread().removeListener(this.display_panel);
    }
}
