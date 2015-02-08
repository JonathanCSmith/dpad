package jonathansmith.dpad.common.engine.executor;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * A task to be executed in a thread environment.
 */
public abstract class Task extends Thread {

    protected final Engine loggingEngine;
    private final   String taskName;
    private boolean isFinished = false;

    /**
     * Construct the task
     *
     * @param taskName
     * @param engine
     */
    public Task(String taskName, Engine engine) {
        this.taskName = taskName;
        this.loggingEngine = engine;
    }

    /**
     * Function called to actually run the task
     */
    protected abstract void runTask();

    /**
     * Overridable function to kill the task early
     */
    protected void killTask() {
    }

    /**
     * Normal runtime called by executors
     */
    @Override
    public final void run() {
        this.runTask();
        this.setFinished();
    }

    /**
     * @return a string description of the task name.
     */
    public final String getTaskName() {
        return this.taskName;
    }

    /**
     * Kill the task when desired
     */
    public final void kill() {
        this.killTask();
        this.isFinished = true;
    }

    /**
     * Allows internal markup of when a task is finished. This should be called when the task is finished to
     * allow the executor to determine that it is finished
     */
    protected final void setFinished() {
        this.isFinished = true;
    }

    /**
     * Query the state of the task.
     *
     * @return whether the task is finished or not
     */
    public final boolean isFinished() {
        return this.isFinished;
    }
}
