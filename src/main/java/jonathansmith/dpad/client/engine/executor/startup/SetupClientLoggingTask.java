package jonathansmith.dpad.client.engine.executor.startup;

import org.apache.log4j.Level;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Task;
import jonathansmith.dpad.common.engine.util.log.LoggerFactory;
import jonathansmith.dpad.common.engine.util.log.LoggingLevel;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Task for setting up the client logging framework.
 */
public class SetupClientLoggingTask extends Task {

    private static final String TASK_NAME = "Client Logging Setup";

    private final Engine engine;

    public SetupClientLoggingTask(Engine engine) {
        super(TASK_NAME, engine);
        this.engine = engine;
    }

    @Override
    public void runTask() {
        // Bind the net logger to our loggers
        LoggerFactory.getInstance().getLogger(this.engine, "io.netty", new LoggingLevel(Level.DEBUG, Level.WARN, Level.TRACE, Level.INFO));

        this.loggingEngine.trace("Client logging setup complete", null);
    }
}
