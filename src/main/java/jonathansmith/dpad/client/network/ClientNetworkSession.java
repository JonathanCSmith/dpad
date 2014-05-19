package jonathansmith.dpad.client.network;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.login.LoginStartPacket;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network session
 */
public class ClientNetworkSession extends NetworkSession {

    public ClientNetworkSession(IEngine engine, boolean isLocal) {
        super(engine, isLocal);

        this.setNetworkProtocol(new ClientLoginProtocol(this.engine, this));
        this.scheduleOutboundPacket(new LoginStartPacket(this.engine.getVersion(), this), new GenericFutureListener[0]);
    }
}
