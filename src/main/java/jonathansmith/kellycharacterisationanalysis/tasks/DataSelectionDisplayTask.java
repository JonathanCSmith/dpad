package jonathansmith.kellycharacterisationanalysis.tasks;

import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.display.selection.DatasetSelectionDisplay;

/**
 * Created by Jon on 29/09/2014.
 */
public class DataSelectionDisplayTask implements IPluginTask {

    private static final String TASK_NAME = "Select data";

    private final KellyCharacterisationAnalysis core;

    public DataSelectionDisplayTask(KellyCharacterisationAnalysis kellyCharacterisationAnalysis) {
        this.core = kellyCharacterisationAnalysis;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime runtime) {
        IPluginDisplay pluginDisplay = new DatasetSelectionDisplay(this.core, runtime);
        runtime.changeDisplay(pluginDisplay);
    }

    @Override
    public void killTask(IPluginRuntime runtime) {

    }
}
