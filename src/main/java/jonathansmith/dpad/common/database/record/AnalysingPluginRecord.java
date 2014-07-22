package jonathansmith.dpad.common.database.record;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Represents database information regarding an analysing plugin
 */
@Entity
@Table(name = "Analysing_Plugins")
public class AnalysingPluginRecord extends PluginRecord {

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
