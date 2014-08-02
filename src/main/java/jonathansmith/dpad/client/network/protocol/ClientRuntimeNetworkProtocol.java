package jonathansmith.dpad.client.network.protocol;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.play.KeepAlivePacket;
import jonathansmith.dpad.common.network.packet.play.user.UserAdministrationResponsePacket;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ServerUserResponseEvent;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Client runtime protocol. Responsible for all packet specific functions. Storing the functions here prevents external packet influence on engine state.
 */
public class ClientRuntimeNetworkProtocol extends ClientNetworkProtocol implements IRuntimeNetworkProtocol {

    private static final String PROTOCOL_NAME = "Client Runtime Protocol";

    public ClientRuntimeNetworkProtocol(ClientEngine engine, NetworkSession session) {
        super(engine, session, PROTOCOL_NAME);
    }

    public void handleUserAdministrationResponse(UserAdministrationResponsePacket userAdministrationResponsePacket) {
        this.engine.getEventThread().postEvent(new ServerUserResponseEvent(userAdministrationResponsePacket.getState()));
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
