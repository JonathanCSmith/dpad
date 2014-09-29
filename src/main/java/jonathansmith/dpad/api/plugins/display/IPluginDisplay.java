package jonathansmith.dpad.api.plugins.display;

/**
 * Created by Jon on 29/09/2014.
 */
public interface IPluginDisplay {

    DisplayPanel getDisplayToolbar();

    DisplayPanel getDisplayPanel();

    void onDisplayActivation();

    void onDisplayUpdate();

    void onDisplayDestroy();
}
