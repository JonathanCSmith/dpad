package jonathansmith.microplatetxtloader.tasks;

import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;
import jonathansmith.microplatetxtloader.display.secondstage.MicroplateLoaderSecondDisplay;

/**
 * Created by Jon on 29/09/2014.
 */
public class BuildSecondDisplayTask implements IPluginTask {

    private static final String TASK_NAME = "Build display 2";

    private final MicroplateTXTLoader core;

    public BuildSecondDisplayTask(MicroplateTXTLoader microplateTXTLoader) {
        this.core = microplateTXTLoader;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime runtime) {
        if (this.core.isQuittingEarly()) {
            return;
        }

        IPluginDisplay pluginDisplay = new MicroplateLoaderSecondDisplay(this.core, runtime);
        runtime.changeDisplay(pluginDisplay);
    }

    @Override
    public void killTask(IPluginRuntime runtime) {

    }
}
