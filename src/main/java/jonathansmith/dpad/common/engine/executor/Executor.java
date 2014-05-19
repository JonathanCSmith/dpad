package jonathansmith.dpad.common.engine.executor;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Abstract operation parent. Represents an internal execution thread.
 */
public abstract class Executor extends Thread {

    protected final Engine engine;

    private boolean isExecuting = false;
    private boolean hasFinished = false;

    public Executor(Engine engine) {
        this.engine = engine;
    }

    public final void execute() {
        this.isExecuting = true;
        this.start();
    }

    public boolean hasFinished() {
        return this.hasFinished;
    }

    protected void setFinished() {
        this.hasFinished = true;
    }

    public abstract void shutdown(boolean forceShutdownFlag);

    public final boolean isExecuting() {
        return this.isExecuting;
    }
}
