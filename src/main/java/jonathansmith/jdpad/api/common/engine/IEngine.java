package jonathansmith.jdpad.api.common.engine;

import org.slf4j.Logger;

import jonathansmith.jdpad.api.common.engine.event.IEventThread;

import jonathansmith.jdpad.common.engine.executor.Executor;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Utility methods for engine use
 */
public interface IEngine {

    boolean isViable();

    Logger getLogger();

    void setCurrentExecutor(Executor op);

    Executor getCurrentExecutor();

    boolean hasErrored();

    boolean isShuttingDown();

    IEventThread getEventThread();

    void startShutdown();

    void trace(String message, Throwable e);

    void debug(String message, Throwable e);

    void info(String message, Throwable e);

    void warn(String message, Throwable e);

    void error(String message, Throwable e);

    void handleError(String message, Throwable e, boolean shutdownThreadFlag);
}
