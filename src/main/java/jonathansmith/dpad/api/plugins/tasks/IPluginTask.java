package jonathansmith.dpad.api.plugins.tasks;

import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * A contract for plugin tasks. Objects implementing this will be automatically run in a thread to perform the task.
 */
public interface IPluginTask {

    /**
     * Return the task name
     *
     * @return
     */
    String getTaskName();

    /**
     * Function called when the task is going to be run.
     */
    void runTask(IPluginRuntime runtime);

    /**
     * Function called to force code within the runTask function to finish early.
     */
    void killTask(IPluginRuntime runtime);
}
