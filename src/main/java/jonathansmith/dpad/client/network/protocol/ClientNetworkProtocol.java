package jonathansmith.dpad.client.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 19/05/2014.
 *
 * Client side specific protocols
 */
public abstract class ClientNetworkProtocol extends NetworkProtocol {

    public ClientNetworkProtocol(IEngine engine, NetworkSession session) {
        super(engine, session);
    }

    /**
     * Used to notify the network protocol of an impending disconnect.
     *
     * @param reason
     */
    public abstract void handleDisconnect(String reason);
}
