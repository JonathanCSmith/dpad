package jonathansmith.kellycharacterisationwithod.tasks;

import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationwithod.KellyCharacterisationWithOD;
import jonathansmith.kellycharacterisationwithod.display.selection.DatasetSelectionDisplay;

/**
 * Created by Jon on 09/11/2014.
 */
public class DisplayDataSelectionTask implements IPluginTask {

    private static final String TASK_NAME = "Select Data";

    private final KellyCharacterisationWithOD parent;

    public DisplayDataSelectionTask(KellyCharacterisationWithOD kellyCharacterisationWithOD) {
        this.parent = kellyCharacterisationWithOD;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime runtime) {
        IPluginDisplay pluginDisplay = new DatasetSelectionDisplay(this.parent, runtime);
        runtime.changeDisplay(pluginDisplay);
    }

    @Override
    public void killTask(IPluginRuntime runtime) {

    }
}
