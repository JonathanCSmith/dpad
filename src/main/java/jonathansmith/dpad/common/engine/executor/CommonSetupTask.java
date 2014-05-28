package jonathansmith.dpad.common.engine.executor;

import org.apache.log4j.Level;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.event.EventThread;
import jonathansmith.dpad.common.engine.event.gui.ProgressBarUpdateEvent;
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

    public CommonSetupTask(Engine engine) {
        super(TASK_NAME, engine);

        this.engine = engine;
    }

    @Override
    public void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 4, 0));

        // Build the filesystem for the engine
        this.engine.setFileSystem(new FileSystem(this.engine));
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 4, 1));

        // Build the generic logger for both
        this.engine.setLogger(LoggerFactory.getInstance().getLogger(this.engine, new LoggingLevel(Level.DEBUG, Level.WARN, Level.TRACE, Level.INFO)));
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 4, 2));

        // Start the event thread NOTE: I am not particularly happy with this cast, but it seems better than putting the start method in the api interface...
        ((EventThread) this.engine.getEventThread()).start();
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 4, 3));

        // Start the plugin thread
        this.engine.getPluginManager().start();
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 4, 4));
    }
}
