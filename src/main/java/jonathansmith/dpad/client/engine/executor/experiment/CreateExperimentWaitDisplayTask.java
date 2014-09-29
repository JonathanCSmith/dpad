package jonathansmith.dpad.client.engine.executor.experiment;

import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.home.ProgressbarDisplay;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Task to create the wait display whilst experiments are being sent
 */
public class CreateExperimentWaitDisplayTask extends Task {

    private static final String TASK_NAME = "Experiment Wait";

    public CreateExperimentWaitDisplayTask(Engine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ProgressbarDisplay((ClientEngine) this.loggingEngine)));
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Notifying the server of required experiments", 0, 2, 0));
    }
}
