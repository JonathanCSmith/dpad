package jonathansmith.jdpad.common.database.record;

import java.io.Serializable;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Super class for all database entries. Inherently serializable for network transport.
 * Contains a UUID for database entry management as well as hashcoding and implied equals methods.
 */
public class Record implements Serializable {

}
