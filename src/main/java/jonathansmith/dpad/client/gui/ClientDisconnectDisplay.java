package jonathansmith.dpad.client.gui;

import javax.swing.*;

import jonathansmith.dpad.api.common.engine.IEngine;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Implementation of a client display that appears when the client is disconnected from the server
 */
public class ClientDisconnectDisplay extends ClientDisplay {

    @Override
    public JPanel getToolbarComponent() {
        return null;
    }

    @Override
    public JPanel getDisplayComponent() {
        return null;
    }

    @Override
    public void init(IEngine loggingEngine) {

    }

    @Override
    public void update() {

    }

    @Override
    public void onDestroy(IEngine loggingEngine) {

    }
}
