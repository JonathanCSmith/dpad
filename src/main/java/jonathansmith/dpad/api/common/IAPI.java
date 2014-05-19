package jonathansmith.dpad.api.common;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.common.gui.IGUIController;

/**
 * Created by Jon on 20/05/14.
 * <p/>
 * API methods
 */
public interface IAPI {

    /**
     * Returns whether the API information is fully setup yet
     */
    boolean isAPIViable();

    /**
     * Returns the core GUI Controller for the current running session.
     *
     * @return the {@link jonathansmith.dpad.api.common.gui.IGUIController} instance in this session
     */
    IGUIController getGUI();

    /**
     * Queries whether a server instance is running in this session
     *
     * @return boolean, true if server is running
     */
    boolean isServerRunning();

    /**
     * Returns the current server side engine. This can be null if the current session only pertains to the client side
     *
     * @return the {@link jonathansmith.dpad.api.common.engine.IEngine} instance specific to the server. Can be NULL
     */
    IEngine getServer();

    /**
     * Queries whether a client instance is running in this session
     *
     * @return boolean, true if client is running
     */
    boolean isClientRunning();

    /**
     * Returns the current client side engine. This can be null if the current session only pertains to the server side
     *
     * @return the {@link jonathansmith.dpad.api.common.engine.IEngine} instance specific to the client. Can be NULL
     */
    IEngine getClient();
}
