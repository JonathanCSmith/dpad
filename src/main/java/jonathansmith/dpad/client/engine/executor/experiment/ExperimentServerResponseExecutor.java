package jonathansmith.dpad.client.engine.executor.experiment;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Executor;

import jonathansmith.dpad.client.gui.experiment.ExperimentDisplay;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Executor for querying the server for experiments, displaying a wait panel and rebuilding upon completion
 */
public class ExperimentServerResponseExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Experiment Server Response";

    private final ExperimentDisplay display;

    public ExperimentServerResponseExecutor(Engine engine, ExperimentDisplay display) {
        super(EXECUTOR_NAME, engine, false);

        this.display = display;

        this.addTask(new CreateExperimentWaitDisplayTask(engine));
        this.addTask(new ExperimentResponseWaitTask(engine, display));
    }
}
