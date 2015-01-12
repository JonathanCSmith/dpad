package jonathansmith.dpad.api.database;

import javax.persistence.Entity;
import javax.persistence.Table;

import jonathansmith.dpad.api.plugins.IAnalyserPlugin;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Represents database information regarding an analysing plugin
 */
@Entity
@Table(name = "Analysing_Plugins")
public class AnalysingPluginRecord extends PluginRecord<IAnalyserPlugin> {

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
