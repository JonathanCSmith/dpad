package jonathansmith.microplatetxtloader.tasks;

import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;
import jonathansmith.microplatetxtloader.display.firststage.MicroplateLoaderFirstDisplay;

/**
 * Created by Jon on 29/09/2014.
 */
public class BuildFirstDisplayTask implements IPluginTask {

    private static final String TASK_NAME = "Build Display";

    private final MicroplateTXTLoader core;

    public BuildFirstDisplayTask(MicroplateTXTLoader microplateTXTLoader) {
        this.core = microplateTXTLoader;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime runtime) {
        IPluginDisplay pluginDisplay = new MicroplateLoaderFirstDisplay(this.core, runtime);
        runtime.changeDisplay(pluginDisplay);
    }

    @Override
    public void killTask(IPluginRuntime engine) {

    }
}
