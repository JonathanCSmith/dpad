package jonathansmith.dpad.client.gui.disconnect;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.common.gui.util.BlankToolbar;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Implementation of a client display that appears when the client is disconnected from the server
 */
public class ClientDisconnectDisplay extends ClientDisplay {

    private final DisplayPanel disconnect_toolbar = new BlankToolbar();
    private final DisplayPanel disconnect_panel;

    public ClientDisconnectDisplay(ClientEngine engine, String reason) {
        super(engine);

        this.disconnect_panel = new DisconnectPanel(reason);
    }

    @Override
    public DisplayPanel getToolbarComponent() {
        return this.disconnect_toolbar;
    }

    @Override
    public DisplayPanel getDisplayComponent() {
        return this.disconnect_panel;
    }

    @Override
    public void onActivation() {

    }

    @Override
    public void update() {
        this.disconnect_panel.update();
    }

    @Override
    public void onDestroy() {

    }
}
