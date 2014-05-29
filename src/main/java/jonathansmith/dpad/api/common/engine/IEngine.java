package jonathansmith.dpad.api.common.engine;

import jonathansmith.dpad.api.common.engine.event.IEventThread;
import jonathansmith.dpad.api.common.util.Version;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Utility methods for engine use. Mainly used as a reminder to synchronize methods that are likely to be accessed from multiple threads.
 */
public interface IEngine {

    /**
     * Return the version of this engine. Includes the network protocol version string for comparing client / server compatibility
     *
     * @return String version information
     */
    Version getVersion();

    /**
     * Return the event thread for the engine
     *
     * @return {@link jonathansmith.dpad.api.common.engine.event.IEventThread} the event thread used by the current engine
     */
    IEventThread getEventThread();

    void trace(String message, Throwable e);

    void debug(String message, Throwable e);

    void info(String message, Throwable e);

    void warn(String message, Throwable e);

    void error(String message, Throwable e);

    void handleError(String message, Throwable e);

    /**
     * Causes a shutdown of the engine with full saving.
     *
     * @param exitMessage debug shutdown message
     */
    void handleShutdown(String exitMessage);
}
