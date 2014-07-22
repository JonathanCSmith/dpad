package jonathansmith.dpad.common;

import jonathansmith.dpad.api.common.IAPI;
import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.common.gui.IGUIController;

/**
 * Created by Jon on 20/05/14.
 * <p/>
 * Core implementation of the API. Hidden from external packages so that variables cannot be modified.
 */
public class API implements IAPI {

    private boolean        apiSetup = false;
    private IGUIController gui      = null;
    private IEngine        server   = null;
    private IEngine        client   = null;

    @Override
    public boolean isAPIViable() {
        return this.apiSetup;
    }

    public void finishAPISetup() {
        this.apiSetup = true;
    }

    @Override
    public IGUIController getGUI() {
        return this.gui;
    }

    public void setGUI(IGUIController gui) {
        this.gui = gui;
    }

    @Override
    public IEngine getServer() {
        return this.server;
    }

    public void setServerEngine(IEngine server) {
        if (server == null || this.server != null) {
            return;
        }

        this.server = server;
    }

    @Override
    public IEngine getClient() {
        return this.client;
    }

    public void setClientEngine(IEngine client) {
        if (client == null || this.client != null) {
            return;
        }

        this.client = client;
    }
}
