package jonathansmith.dpad.common.engine.executor;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 03/04/14.
 * <p/>
 * Generic idle executor for both client and server. Used when no operations are being performed on the main engine thread.
 */
public class IdleExecutor extends Executor {

    public IdleExecutor(Engine engine) {
        super(engine);
    }

    @Override
    public void execute() {
        while (!this.hasFinished() && !this.engine.hasErrored()) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                this.engine.handleError("Engine idle executor was interrupted", ex, true);
            }
        }
    }

    @Override
    public void shutdown(boolean forceShutdownFlag) {

    }
}
