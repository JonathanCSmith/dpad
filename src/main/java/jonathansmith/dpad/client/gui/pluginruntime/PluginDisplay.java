package jonathansmith.dpad.client.gui.pluginruntime;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.display.IPluginDisplay;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 29/09/2014.
 */
public class PluginDisplay extends ClientDisplay {

    private final IPluginDisplay plugin_display;

    public PluginDisplay(ClientEngine engine, IPluginDisplay pluginDisplay) {
        super(engine);

        this.plugin_display = pluginDisplay;
    }

    @Override
    public DisplayPanel getToolbarComponent() {
        return this.plugin_display.getDisplayToolbar();
    }

    @Override
    public DisplayPanel getDisplayComponent() {
        return this.plugin_display.getDisplayPanel();
    }

    @Override
    public void onActivation() {
        this.plugin_display.onDisplayActivation();
    }

    @Override
    public void update() {
        this.plugin_display.onDisplayUpdate();
    }

    @Override
    public void onDestroy() {
        this.plugin_display.onDisplayDestroy();
    }
}
