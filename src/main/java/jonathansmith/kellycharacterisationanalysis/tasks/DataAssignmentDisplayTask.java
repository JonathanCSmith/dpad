package jonathansmith.kellycharacterisationanalysis.tasks;

import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.display.assignment.DataAssignmentDisplay;

/**
 * Created by Jon on 29/09/2014.
 */
public class DataAssignmentDisplayTask implements IPluginTask {

    private static final String TASK_NAME = "Assign data types";

    private final KellyCharacterisationAnalysis core;

    public DataAssignmentDisplayTask(KellyCharacterisationAnalysis kellyCharacterisationAnalysis) {
        this.core = kellyCharacterisationAnalysis;
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

        runtime.changeDisplay(new DataAssignmentDisplay(this.core, runtime));
    }

    @Override
    public void killTask(IPluginRuntime runtime) {

    }
}
