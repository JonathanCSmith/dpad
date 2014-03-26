package jonathansmith.jdpad.server.engine.executor;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.engine.executor.Executor;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Server idle executor
 */
public class ServerIdle extends Executor {

    public ServerIdle(Engine engine) {
        super(engine);
    }

    @Override
    public void execute() {

    }

    @Override
    public void shutdown(boolean forceShutdownFlag) {

    }
}
