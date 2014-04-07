package jonathansmith.dpad.server.network.protocol;

import static jonathansmith.dpad.common.network.ConnectionState.LOGIN;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.DisconnectPacket;
import jonathansmith.dpad.common.network.packet.HandshakePacket;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

import jonathansmith.dpad.server.engine.util.version.Version;

import org.apache.commons.lang3.Validate;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Server handshake protocol for initialising client-server connections
 */
public class ServerHandshakeProtocol extends NetworkProtocol {

    private static final String PROTOCOL_NAME = "Server Login Protocol";

    private final boolean isLocalConnection;

    private boolean versionMatch = false;

    public ServerHandshakeProtocol(IEngine engine, NetworkSession session, boolean isLocal) {
        super(engine, session);
        this.isLocalConnection = isLocal;
    }

    public void onHandshake(HandshakePacket packet) {
        if (this.isLocalConnection) {
            this.networkSession.setConnectionState(packet.getConnectionState());
            this.versionMatch = true;
        }

        else {
            String version = packet.getNetworkProtocolVersion();
            if (Version.isCompatible(this.engine.getVersion(), version)) {
                this.versionMatch = true;
            }
        }
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        Validate.validState(connectionState1 == LOGIN, "Unexpected protocol: " + connectionState1);

        if (this.isLocalConnection || this.versionMatch) {
            this.networkSession.setNetworkProtocol(new ServerLoginProtocol(this.engine, this.networkSession));
        }

        else {
            String reason = "Network protocol version missmatch.";
            this.networkSession.scheduleOutboundPacket(new DisconnectPacket(reason), new GenericFutureListener[0]);
            this.networkSession.closeChannel(reason);
        }
    }

    @Override
    public void pulseRepeatPackets() {

    }

    @Override
    public void onDisconnect(String exitMessage) {

    }

    @Override
    public void handleDisconnect(String reason) {
        this.networkSession.closeChannel(reason);
    }
}
