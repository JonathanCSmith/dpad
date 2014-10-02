package jonathansmith.dpad.api.plugins;

import jonathansmith.dpad.api.plugins.records.ILoaderPluginRecord;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Represents a plugin responsible for loading data into the DPAD software. Usually correlates either with a specific piece of equipment or a specific data layout type
 */
public interface ILoaderPlugin extends IPlugin<ILoaderPluginRecord> {

}
