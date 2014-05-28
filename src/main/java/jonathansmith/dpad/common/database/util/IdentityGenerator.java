package jonathansmith.dpad.common.database.util;

import java.util.UUID;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Unique identification generator for database records.
 */
public class IdentityGenerator {

    public static String createIdentity() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
