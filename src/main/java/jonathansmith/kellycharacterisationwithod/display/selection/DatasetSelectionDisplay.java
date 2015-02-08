package jonathansmith.kellycharacterisationwithod.display.selection;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.kellycharacterisationwithod.KellyCharacterisationWithOD;

/**
 * Created by Jon on 09/11/2014.
 */
public class DatasetSelectionDisplay implements IPluginDisplay {

    private final KellyCharacterisationWithOD parent;
    private final IPluginRuntime              runtime;
    private final DataSelectionToolbarPanel   toolbar_panel;
    private final DataSelectionDisplayPanel   display_panel;


    public DatasetSelectionDisplay(KellyCharacterisationWithOD parent, IPluginRuntime runtime) {
        this.parent = parent;
        this.runtime = runtime;
        this.toolbar_panel = new DataSelectionToolbarPanel(this.parent, this.runtime);
        this.display_panel = new DataSelectionDisplayPanel(this.parent, this.runtime);
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
        this.display_panel.onActivate();
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
