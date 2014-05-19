package jonathansmith.dpad.api.common.engine;

import jonathansmith.dpad.common.network.NetworkManager;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Utility methods for engine use. Mainly used as a reminder to synchronize methods that are likely to be accessed from multiple threads.
 */
public interface IEngine {

    String getVersion();

    void trace(String message, Throwable e);

    void debug(String message, Throwable e);

    void info(String message, Throwable e);

    void warn(String message, Throwable e);

    void error(String message, Throwable e);

    void handleError(String message, Throwable e, boolean shutdownThreadFlag);

    // TODO: Remove from API
    NetworkManager getNetworkManager();
}
