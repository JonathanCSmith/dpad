package jonathansmith.dpad.client.network.session;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.handshake.HandshakeStartPacket;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.network.ClientNetworkManager;
import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network session
 */
public class ClientNetworkSession extends NetworkSession {

    private final ClientNetworkManager networkManager;

    public ClientNetworkSession(ClientEngine engine, ClientNetworkManager manager) {
        super(engine, manager.getSocketAddress(), manager.isLocalConnection(), true);

        this.networkManager = manager;
        this.setNetworkProtocol(new ClientLoginProtocol(engine, this));
        this.scheduleOutboundPacket(new HandshakeStartPacket(engine.getVersion(), this), new GenericFutureListener[0]);
    }
}
