package jonathansmith.dpad.common.engine.executor;

import jonathansmith.dpad.api.common.engine.IEngine;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Parent task for all executors.
 */
public abstract class Task extends Thread {

    protected final IEngine loggingEngine;
    private final   String  taskName;
    private boolean isFinished = false;

    public Task(String taskName, IEngine engine) {
        this.taskName = taskName;
        this.loggingEngine = engine;
    }

    protected abstract void runTask();

    protected void killTask() {
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void kill() {
        this.killTask();
        this.isFinished = true;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    protected void setFinished() {
        this.isFinished = true;
    }

    @Override
    public void run() {
        this.runTask();
        this.setFinished();
    }
}
