package jonathansmith.dpad.common.database.record;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Represents database information regarding a loading plugin
 */
@Entity
@Table(name = "Loading_Plugins")
public class LoadingPluginRecord extends PluginRecord {

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
