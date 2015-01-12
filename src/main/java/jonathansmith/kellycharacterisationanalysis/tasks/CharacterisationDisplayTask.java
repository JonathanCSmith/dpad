package jonathansmith.kellycharacterisationanalysis.tasks;

import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.display.characterisation.CharacterisationDisplay;

/**
 * Created by Jon on 29/09/2014.
 */
public class CharacterisationDisplayTask implements IPluginTask {

    private static final String TASK_NAME = "Build Characterisation Display";

    private final KellyCharacterisationAnalysis core;

    public CharacterisationDisplayTask(KellyCharacterisationAnalysis kellyCharacterisationAnalysis) {
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

        runtime.changeDisplay(new CharacterisationDisplay(core, runtime));
    }

    @Override
    public void killTask(IPluginRuntime runtime) {

    }
}
