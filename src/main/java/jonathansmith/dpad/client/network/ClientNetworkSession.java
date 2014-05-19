package jonathansmith.dpad.client.network;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.login.LoginStartPacket;

import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network session
 */
public class ClientNetworkSession extends NetworkSession {

    private final ClientNetworkManager networkManager;

    public ClientNetworkSession(IEngine engine, ClientNetworkManager manager) {
        super(engine, manager.isLocalConnection());

        this.networkManager = manager;
        this.setNetworkProtocol(new ClientLoginProtocol(this.engine, this));
        this.scheduleOutboundPacket(new LoginStartPacket(this.engine.getVersion(), this), new GenericFutureListener[0]);
    }
}
