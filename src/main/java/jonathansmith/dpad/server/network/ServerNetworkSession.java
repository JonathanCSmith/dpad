package jonathansmith.dpad.server.network;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.network.NetworkSession;

import jonathansmith.dpad.server.network.protocol.ServerHandshakeProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Server side network session
 */
public class ServerNetworkSession extends NetworkSession {

    public ServerNetworkSession(IEngine engine, boolean isLocal) {
        super(engine, isLocal);

        this.setNetworkProtocol(new ServerHandshakeProtocol(this.engine, this, isLocal));
    }
}
