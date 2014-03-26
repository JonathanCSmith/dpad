package jonathansmith.jdpad.server.network;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.network.NetworkSession;

import jonathansmith.jdpad.server.network.protocol.ServerHandshakeProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Server side network session
 */
public class ServerNetworkSession extends NetworkSession {

    public ServerNetworkSession(Engine engine, boolean isLocal) {
        super(engine, isLocal);

        this.setNetworkProtocol(new ServerHandshakeProtocol(this.engine, this, isLocal));
    }
}
