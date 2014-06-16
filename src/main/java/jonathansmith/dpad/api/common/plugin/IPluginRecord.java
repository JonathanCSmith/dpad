package jonathansmith.dpad.api.common.plugin;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Utility methods for obtaining information about a plugin.
 */
public interface IPluginRecord {

    /**
     * Get the plugin name. Should correspond to the jar
     *
     * @return the plugin name where the jar is named pluginName.jar
     */
    String getPluginName();

    /**
     * Description of the plugin and its function
     *
     * @return a description of the plugin
     */
    String getPluginDescription();

    /**
     * Return the author of the plugin.
     * TODO: Move to a list?
     *
     * @return plugin author
     */
    String getPluginAuthor();

    /**
     * Return the organisation associated with the plugin.
     *
     * @return plugin organisation. not necessary
     */
    String getPluginOrganisation();
}
