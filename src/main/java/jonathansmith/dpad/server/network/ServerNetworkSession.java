package jonathansmith.dpad.server.network;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.NetworkSession;

import jonathansmith.dpad.server.network.protocol.ServerLoginNetworkProtocol;
import jonathansmith.dpad.server.network.protocol.ServerRuntimeNetworkProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Server side network session
 */
public class ServerNetworkSession extends NetworkSession {

    private final ServerNetworkManager networkManager;

    public ServerNetworkSession(IEngine engine, ServerNetworkManager manager) {
        super(engine, manager.getSocketAddress(), manager.isLocalConnection(), false);

        this.networkManager = manager;
        this.setNetworkProtocol(new ServerLoginNetworkProtocol(this.engine, this, this.isLocalChannel()));
    }

    public ServerNetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public void finaliseConnection() {
        //this.setConnectionState(ConnectionState.RUNTIME);
        this.setNetworkProtocol(new ServerRuntimeNetworkProtocol(this.engine, this));
    }
}
