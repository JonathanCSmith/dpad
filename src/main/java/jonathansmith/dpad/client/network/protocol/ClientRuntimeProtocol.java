package jonathansmith.dpad.client.network.protocol;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.KeepAlivePacket;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

import jonathansmith.dpad.client.ClientEngine;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Client runtime protocol. Responsible for all packet specific functions. Storing the functions here prevents external packet influence on engine state.
 */
public class ClientRuntimeProtocol extends ClientNetworkProtocol implements IRuntimeNetworkProtocol {

    private static final String PROTOCOL_NAME = "Client Runtime Protocol";

    public ClientRuntimeProtocol(ClientEngine engine, NetworkSession session) {
        super(engine, session, PROTOCOL_NAME);
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        this.engine.error("Unexpected protocol change. Attempted to change to " + connectionState1.toString(), new IllegalStateException("Protocol cannot be changed once runtime has been established"));
        this.network_session.shutdown(true);
    }

    @Override
    public void handleKeepAlive() {
        this.network_session.scheduleOutboundPacket(new KeepAlivePacket(), new GenericFutureListener[0]);
    }
}
