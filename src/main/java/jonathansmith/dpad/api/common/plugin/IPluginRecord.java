package jonathansmith.dpad.api.common.plugin;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Utility methods for obtaining information about a plugin.
 */
public interface IPluginRecord {

    String getPluginName();

    String getPluginDescription();

    // TODO: Move to version
    String getPluginVersion();

    // TODO: Should this be a list?
    String getPluginAuthor();

    String getPluginOrganisation();
}
