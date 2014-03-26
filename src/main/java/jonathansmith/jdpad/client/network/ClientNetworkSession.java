package jonathansmith.jdpad.client.network;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.network.ConnectionState;
import jonathansmith.jdpad.common.network.NetworkSession;
import jonathansmith.jdpad.common.network.packet.HandshakePacket;
import jonathansmith.jdpad.common.network.packet.LoginStartPacket;

import jonathansmith.jdpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network session
 */
public class ClientNetworkSession extends NetworkSession {

    public ClientNetworkSession(Engine engine, boolean isLocal) {
        super(engine, isLocal);

        this.setNetworkProtocol(new ClientLoginProtocol());
        this.scheduleOutboundPacket(new HandshakePacket(this.engine.getVersion(), this.engine.getAddress(), Integer.getInteger(this.engine.getPort()), ConnectionState.LOGIN), new GenericFutureListener[0]);
        this.scheduleOutboundPacket(new LoginStartPacket(), new GenericFutureListener[0]);
    }
}
