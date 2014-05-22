package jonathansmith.dpad.common.engine.executor;

import java.util.LinkedList;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.common.engine.executor.IExecutor;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Abstract operation parent. Represents an internal execution thread.
 */
public abstract class Executor extends Thread implements IExecutor {

    protected final IEngine engine;

    private final LinkedList<Task> task_list = new LinkedList<Task>();

    private final String  executorName;
    private final boolean repeatExecution;

    private int     taskCount    = 0;
    private boolean isExecuting  = false;
    private boolean hasFinished  = false;
    private boolean mustFinish   = false;
    private boolean shutdownFlag = false;

    public Executor(String executorName, Engine engine, boolean repeatingExecutor) {
        this.executorName = executorName;
        this.engine = engine;
        this.repeatExecution = repeatingExecutor;
    }

    public final void execute() {
        this.isExecuting = true;

        if (this.task_list.size() == 0) {
            this.engine.error("Current Executor: " + this.getExecutorName() + " has a task list with no tasks in it. This is stupid and should not happen", null);
            // TODO: Is this fatal?
        }

        this.start();
    }

    @Override
    public final String getExecutorName() {
        return this.executorName;
    }

    @Override
    public final boolean isExecuting() {
        return this.isExecuting;
    }

    @Override
    public final boolean hasFinished() {
        return this.hasFinished;
    }

    @Override
    public final void shutdown(boolean forceShutdownFlag) {
        if (forceShutdownFlag) {
            this.shutdownFlag = true;
        }

        this.mustFinish = true;
    }

    protected final void addTask(Task task) {
        this.task_list.add(task);
    }

    @Override
    public final void run() {
        if (this.repeatExecution) {
            while (!this.mustFinish) {
                this.runTasks();
                this.engine.trace("Repeating task set in current executor: " + this.executorName, null);
            }
        }

        else {
            this.engine.trace("Executing: " + this.executorName, null);
            this.runTasks();
        }

        this.isExecuting = false;
        this.hasFinished = true;
    }

    // Note: No must finish flags here as the premise is that the executor be allowed to complete all of its tasks
    private void runTasks() {
        while (!this.shutdownFlag && this.taskCount < this.task_list.size()) {
            Task task = this.task_list.get(this.taskCount);
            this.engine.trace("Starting task: " + task.getTaskName(), null);
            task.runTask();
            this.taskCount++;
        }
    }
}
