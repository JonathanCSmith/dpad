package jonathansmith.microplatetxtloader.display.secondstage;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;

/**
 * Created by Jon on 29/09/2014.
 */
public class MicroplateLoaderSecondDisplay implements IPluginDisplay {

    private final MicroplateTXTLoader          core;
    private final IPluginRuntime               runtime;
    private final MicroplateToolbarSecondPanel toolbar_panel;
    private final MicroplateDisplaySecondPanel display_panel;

    public MicroplateLoaderSecondDisplay(MicroplateTXTLoader core, IPluginRuntime runtime) {
        this.core = core;
        this.runtime = runtime;
        this.toolbar_panel = new MicroplateToolbarSecondPanel(core, runtime);
        this.display_panel = new MicroplateDisplaySecondPanel(core, runtime);
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
