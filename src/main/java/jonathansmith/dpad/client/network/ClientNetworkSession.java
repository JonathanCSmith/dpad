package jonathansmith.dpad.client.network;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.HandshakePacket;
import jonathansmith.dpad.common.network.packet.LoginStartPacket;

import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network session
 */
public class ClientNetworkSession extends NetworkSession {

    public ClientNetworkSession(IEngine engine, boolean isLocal) {
        super(engine, isLocal);

        this.setNetworkProtocol(new ClientLoginProtocol(this.engine, this));
        this.scheduleOutboundPacket(new HandshakePacket(this.engine.getVersion(), this.engine.getAddress(), Integer.getInteger(this.engine.getPort()), ConnectionState.LOGIN), new GenericFutureListener[0]);
        this.scheduleOutboundPacket(new LoginStartPacket(), new GenericFutureListener[0]);
    }
}
