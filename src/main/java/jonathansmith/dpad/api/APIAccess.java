package jonathansmith.dpad.api;

import jonathansmith.dpad.api.common.IAPI;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Core APIAccess class for DPAD
 */
public class APIAccess {

    private static boolean apiLock = false;
    private static IAPI api;

    /**
     * Return the API instance. Note special consideration should be given to the {@link jonathansmith.dpad.api.common.IAPI#isAPIViable()} as the api cannot be trusted until this returns true
     *
     * @return {@link jonathansmith.dpad.api.common.IAPI} instance for this session.
     */
    public static IAPI getAPI() {
        return api;
    }

    public static void setAPI(IAPI iapi) {
        if (apiLock) {
            return;
        }

        if (iapi == null || api != null) {
            return;
        }

        api = iapi;
        apiLock = true;
    }
}
