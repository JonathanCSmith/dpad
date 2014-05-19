package jonathansmith.dpad.server.engine.executor;

import org.apache.log4j.Level;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Task;
import jonathansmith.dpad.common.engine.util.log.LoggerFactory;
import jonathansmith.dpad.common.engine.util.log.LoggingLevel;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Server logging setup task
 */
public class SetupServerLoggingTask extends Task {

    private static final String TASK_NAME = "Server Logging Setup";

    private final Engine engine;

    public SetupServerLoggingTask(Engine engine) {
        super(TASK_NAME, engine);

        this.engine = engine;
    }

    @Override
    public void runTask() {
        // Bind the net logger to our loggers
        LoggerFactory.getInstance().getLogger(this.engine, "org.jboss.logging", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));
        LoggerFactory.getInstance().getLogger(this.engine, "io.netty", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));
        LoggerFactory.getInstance().getLogger(this.engine, "org.hibernate", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));

        this.loggingEngine.trace("Server logging setup complete", null);
    }
}
