package jonathansmith.dpad.api.plugins;

import java.util.LinkedList;

import net.xeoh.plugins.base.Plugin;

import jonathansmith.dpad.api.plugins.records.IPluginRecord;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Core plugin class. Not to be used directly. Merely inherited either by
 * {@link ILoaderPlugin}
 * or {@link IAnalyserPlugin}
 * To implement this class you must compile with jspf found at <a href="https://code.google.com/p/jspf/">JSPF</a>
 */
public interface IPlugin<T extends IPluginRecord> extends Plugin {

    /**
     * Return the plugin record type
     *
     * @return either an ILoaderPluginRecord or an IAnalyserPluginRecord depending on the type of plugin
     */
    T getPluginRecord();

    /**
     * Return the list of tasks that this plugin should run when the user selects it
     *
     * @return
     */
    LinkedList<IPluginTask> getPluginRuntimeTasks();
}
