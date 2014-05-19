package jonathansmith.dpad.common.engine.executor;

import jonathansmith.dpad.api.common.engine.IEngine;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Parent task for all executors.
 */
public abstract class Task {

    protected final IEngine loggingEngine;

    private final String taskName;

    public Task(String taskName, IEngine engine) {
        this.taskName = taskName;
        this.loggingEngine = engine;
    }

    public abstract void runTask();

    public String getTaskName() {
        return this.taskName;
    }
}
