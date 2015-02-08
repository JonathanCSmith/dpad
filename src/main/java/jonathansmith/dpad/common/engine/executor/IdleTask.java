package jonathansmith.dpad.common.engine.executor;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Generic Idle Task.
 */
public class IdleTask extends Task {

    private static final String TASK_NAME = "Idle";

    public IdleTask(Engine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    public void runTask() {
        try {
            Thread.sleep(100);
        }

        catch (InterruptedException ex) {
            // Don't care?!
        }
    }
}
