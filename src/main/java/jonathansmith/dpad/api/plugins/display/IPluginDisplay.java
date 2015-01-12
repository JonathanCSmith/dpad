package jonathansmith.dpad.api.plugins.display;

/**
 * Created by Jon on 29/09/2014.
 */
public interface IPluginDisplay {

    /**
     * Return the display panel responsible for handling your toolbar
     *
     * @return
     */
    DisplayPanel getDisplayToolbar();

    /**
     * Return the display panel that will act as the core of your plugin
     *
     * @return
     */
    DisplayPanel getDisplayPanel();

    /**
     * Called when a display becomes active
     */
    void onDisplayActivation();

    /**
     * Called when a display is updated
     */
    void onDisplayUpdate();

    /**
     * Called when a display is destroyed
     */
    void onDisplayDestroy();
}
