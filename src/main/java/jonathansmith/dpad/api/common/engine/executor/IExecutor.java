package jonathansmith.dpad.api.common.engine.executor;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Class representing an executor within the engine. Note: External applications cannot implement an executor directly.
 * Instead they can query it's state etc. This may change in the future.
 */
public interface IExecutor {

    /**
     * Query the name of the selected executor
     *
     * @return the unique name of the current executor
     */
    String getExecutorName();

    /**
     * Query the current state of the executor
     *
     * @return boolean whether the executor is currently executing
     */
    boolean isExecuting();

    /**
     * Query the finished state of the executor
     *
     * @return boolean whether the executor is finished executing
     */
    boolean hasFinished();

    /**
     * Shutdown the current execution. The force shutdown is specifically applicable to repeating tasks which can be allowed to finish their current task list but not repeat by setting this to false.
     *
     * @param forceShutdownFlag boolean whether to force the shutdown of the executor (i.e. end early without finishing tasks).
     */
    void shutdown(boolean forceShutdownFlag);
}
