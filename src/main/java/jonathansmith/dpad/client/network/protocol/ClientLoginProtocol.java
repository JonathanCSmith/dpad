package jonathansmith.dpad.client.network.protocol;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.commons.lang3.Validate;

import jonathansmith.dpad.common.crypto.CryptographyManager;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.handshake.EncryptionResponsePacket;
import jonathansmith.dpad.common.network.packet.handshake.HandshakeConfirmPacket;
import jonathansmith.dpad.common.network.packet.handshake.HandshakeSuccessPacket;

import jonathansmith.dpad.client.ClientEngine;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client login protocol. No encryption used...
 */
public class ClientLoginProtocol extends ClientNetworkProtocol {

    private static final String PROTOCOL_NAME = "Client Login Protocol";

    public ClientLoginProtocol(ClientEngine engine, NetworkSession session) {
        super(engine, session, PROTOCOL_NAME);
    }

    public void handleEncryptionRequest(PublicKey key, byte[] randomSignature) {
        final SecretKey secretKey = CryptographyManager.createNewSharedKey();
        this.network_session.scheduleOutboundPacket(new EncryptionResponsePacket(secretKey, key, randomSignature), new GenericFutureListener[]{
                new GenericFutureListener() {
                    @Override
                    public void operationComplete(Future future) throws Exception {
                        ClientLoginProtocol.this.network_session.enableEncryption(secretKey);
                    }
                }
        });
    }

    public void handleLoginSuccess(HandshakeSuccessPacket handshakeSuccessPacket) {
        this.network_session.assignForeignUUID(handshakeSuccessPacket.getUUIDPayload());
        this.network_session.scheduleOutboundPacket(new HandshakeConfirmPacket(this.network_session.getEngineAssignedUUID().toString(), this.network_session.getForeignUUID().toString()), new GenericFutureListener[]{
                new GenericFutureListener() {
                    @Override
                    public void operationComplete(Future future) throws Exception {
                        ClientLoginProtocol.this.network_session.setConnectionState(ConnectionState.RUNTIME);
                    }
                }
        });
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        this.engine.debug("Switching from: " + (connectionState == null ? "NULL" : connectionState.toString()) + " to: " + (connectionState1 == null ? "NULL" : connectionState1.toString()), null);

        try {
            Validate.validState((connectionState == null && connectionState1 == ConnectionState.HANDSHAKE) || (connectionState == ConnectionState.HANDSHAKE && connectionState1 == ConnectionState.RUNTIME), "Cannot switch from connection state %s to %s", connectionState == null ? "NULL" : connectionState.toString(), connectionState1 == null ? "NULL" : connectionState1.toString());
        }

        catch (IllegalStateException ex) {
            this.engine.error("Invalid connection state transition", ex);
            this.network_session.shutdown(true);
        }

        if (connectionState1 == ConnectionState.RUNTIME) {
            this.network_session.setNetworkProtocol(new ClientRuntimeNetworkProtocol(this.engine, this.network_session));
        }
    }
}
