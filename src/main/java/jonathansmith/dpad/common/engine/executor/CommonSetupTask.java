package jonathansmith.dpad.common.engine.executor;

import org.apache.log4j.Level;

import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.io.FileSystem;
import jonathansmith.dpad.common.engine.util.log.LoggerFactory;
import jonathansmith.dpad.common.engine.util.log.LoggingLevel;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Common setup tasks to both client and server
 */
public class CommonSetupTask extends Task {

    private static final String TASK_NAME = "Common Setup";

    private final Engine engine;
    //private final ServerConfiguration config;

    public CommonSetupTask(Engine engine) {
        super(TASK_NAME, engine);

        this.engine = engine;
        //this.config = configTask.getServerSetupConfiguration();
    }

    @Override
    public void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 0));

        // Build the filesystem for the engine
        this.engine.setFileSystem(new FileSystem(this.engine));
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 1));

        // Build the generic logger for both
        this.engine.setLogger(LoggerFactory.getInstance().getLogger(this.engine, new LoggingLevel(Level.DEBUG, Level.WARN, Level.TRACE, Level.INFO)));
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 2));

        // Start the plugin thread
        this.engine.getPluginManager().start();
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 3));
        this.setFinished();
    }
}
