package jonathansmith.dpad.api.common.gui;

import javax.swing.*;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Methods for allowing a tab to be displayed within the core GUI
 */
public interface ITabController {

    /**
     * The title of the tab to be displayed
     *
     * @return String
     */
    String getTitle();

    /**
     * Return the content displayed within the tab. Note it must be a JPanel instance or derivative
     *
     * @return {@link javax.swing.JPanel}
     */
    JPanel getPanel();

    /**
     * Generic function called when the display is preparing. Allows setup of variables etc.
     */
    void init();

    /**
     * Method called during a GUI update. Can be used to reasses the state of the tab.
     */
    void update();

    /**
     * Event when the window is closing
     */
    void onWindowClosing();

    /**
     * Event when the window is closed
     */
    void onWindowClosed();

    /**
     * Used to notify the tab when it should shutdown its current processes
     *
     * @param force when true it indicates that an upstream error has occured and data integrity throughout the system cannot be guaranteed. Shutdown should be tenuous about any data it stores.
     */
    void shutdown(boolean force);
}
