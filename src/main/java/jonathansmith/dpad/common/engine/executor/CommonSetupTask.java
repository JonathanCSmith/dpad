package jonathansmith.dpad.common.engine.executor;

import org.apache.log4j.Level;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.event.EventThread;
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
        // Build the filesystem for the engine
        this.engine.setFileSystem(new FileSystem(this.engine));

        // Build the generic logger for both
        this.engine.setLogger(LoggerFactory.getInstance().getLogger(this.engine, new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO)));

        // Start the event thread NOTE: I am not particularly happy with this cast, but it seems better than putting the start method in the api interface...
        ((EventThread) this.engine.getEventThread()).start();

        // TODO: Plugin manager startup
    }
}
