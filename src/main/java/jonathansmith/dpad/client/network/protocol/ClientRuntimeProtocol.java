package jonathansmith.dpad.client.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Client runtime protocol. Responsible for all packet specific functions. Storing the functions here prevents external packet influence on engine state.
 */
public class ClientRuntimeProtocol extends ClientNetworkProtocol {

    private static final String PROTOCOL_NAME = "Client Runtime Protocol";

    public ClientRuntimeProtocol(IEngine engine, NetworkSession session) {
        super(engine, session, PROTOCOL_NAME);
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        this.engine.handleError("Unexpected protocol change. Attempted to change to" + connectionState1.toString(), new IllegalStateException("Protocol cannot be changed once runtime has been established"), false);
    }
}
