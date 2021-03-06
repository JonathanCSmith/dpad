package jonathansmith.dpad.client;

import java.net.SocketAddress;

import jonathansmith.dpad.api.common.network.session.ISessionData;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.network.ISession;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.client.engine.executor.idle.ClientIdleExecutor;
import jonathansmith.dpad.client.engine.executor.startup.ClientStartupExecutor;
import jonathansmith.dpad.client.gui.ClientTabController;
import jonathansmith.dpad.client.network.ClientNetworkManager;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client engine. Implementation of a client side runtime engine, driving the actual program.
 */
public class ClientEngine extends Engine {

    private final ClientIdleExecutor executor;

    public ClientEngine(SocketAddress address) {
        super(address, new ClientTabController());

        this.engine_tab_controller.setEngine(this);
        DPAD.getInstance().getGUI().addTab(this.engine_tab_controller);

        // Add the client startup executor as the first program to be run. Ensuring that everything is setup before anything else is performed.
        this.setProposedExecutorWithoutWaiting(new ClientStartupExecutor(this, this.address));
        this.executor = new ClientIdleExecutor(this);
    }

    public ISession getSession() {
        return ((ClientNetworkManager) this.getNetworkManager()).getSession();
    }

    public ISessionData getSessionData() {
        return ((ClientNetworkManager) this.getNetworkManager()).getSession().getSessionData();
    }

    @Override
    public Platform getPlatform() {
        return Platform.CLIENT;
    }

    @Override
    protected Executor getDefaultExecutor() {
        return this.executor;
    }
}
