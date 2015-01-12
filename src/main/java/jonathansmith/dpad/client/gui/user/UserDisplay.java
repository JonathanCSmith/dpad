package jonathansmith.dpad.client.gui.user;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.gui.ClientDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * User administration display parent
 */
public class UserDisplay extends ClientDisplay {

    private final DisplayPanel            toolbar_panel;
    private final UserAdministrationPanel display_panel;

    public UserDisplay(ClientEngine engine) {
        super(engine);

        this.toolbar_panel = new UserAdministrationToolbar(engine);
        this.display_panel = new UserAdministrationPanel(engine);
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
        this.toolbar_panel.update();
        this.display_panel.update();
    }

    @Override
    public void onDestroy() {
        this.engine.getEventThread().removeListener(this.display_panel);
    }
}
