package jonathansmith.dpad.api.common.plugin;

import net.xeoh.plugins.base.Plugin;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Core plugin class. Not to be used directly. Merely inherited either by {@link jonathansmith.dpad.api.common.plugin.ILoaderPlugin} or {@link jonathansmith.dpad.api.common.plugin.IAnalyserPlugin}
 * To implement this class you must compile with jspf found at <a href="https://code.google.com/p/jspf/">JSPF</a>
 */
public interface IPlugin extends Plugin {

    // TODO: Parse version information from file name instead
    IPluginRecord getPluginRecord();
}
