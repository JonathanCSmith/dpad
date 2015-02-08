package jonathansmith.dpad.common.engine.executor;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.event.EventThread;

/**
 * Created by Jon on 13/07/2014.
 * <p/>
 * Task to start the event thread.
 */
public class EventThreadStartTask extends Task {

    private static final String TASK_NAME = "Event thread setup";

    public EventThreadStartTask(Engine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        // Start the event thread NOTE: I am not particularly happy with this cast, but it seems better than putting the start method in the api interface...
        ((EventThread) this.loggingEngine.getEventThread()).start();
    }
}
