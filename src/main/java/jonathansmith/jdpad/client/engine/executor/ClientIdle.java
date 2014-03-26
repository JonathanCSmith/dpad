package jonathansmith.jdpad.client.engine.executor;

import jonathansmith.jdpad.common.engine.executor.Executor;

import jonathansmith.jdpad.client.ClientEngine;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client idle operation
 */
public class ClientIdle extends Executor {

    public ClientIdle(ClientEngine engine) {
        super(engine);
    }

    @Override
    public void execute() {
        try {
            Thread.sleep(100);
        }

        catch (InterruptedException ex) {
            // As we are idling data is likely to be entirely valid, so do not force shutdown
            this.engine.handleError("Idle executor was interrupted. While not an error per-se this will shutdown the client", null, false);
            this.engine.saveAndShutdown();
        }
    }

    @Override
    public void shutdown(boolean forceShutdownFlag) {
    }
}
