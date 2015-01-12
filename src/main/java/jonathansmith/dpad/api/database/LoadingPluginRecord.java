package jonathansmith.dpad.api.database;

import javax.persistence.Entity;
import javax.persistence.Table;

import jonathansmith.dpad.api.plugins.ILoaderPlugin;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Represents database information regarding a loading plugin
 */
@Entity
@Table(name = "Loading_Plugins")
public class LoadingPluginRecord extends PluginRecord<ILoaderPlugin> {

    @Override
    public void buildRecordFromPlugin(ILoaderPlugin plugin) {
        super.buildRecordFromPlugin(plugin);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
