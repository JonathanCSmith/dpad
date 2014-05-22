package jonathansmith.dpad.client.network.protocol;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.crypto.CryptographyManager;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.login.EncryptionResponsePacket;
import jonathansmith.dpad.common.network.packet.login.LoginConfirmPacket;
import jonathansmith.dpad.common.network.packet.login.LoginSuccessPacket;

import org.apache.commons.lang3.Validate;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client login protocol. No encryption used...
 */
public class ClientLoginProtocol extends ClientNetworkProtocol {

    private static final String PROTOCOL_NAME = "Client Login Protocol";

    public ClientLoginProtocol(IEngine engine, NetworkSession session) {
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

    public void handleLoginSuccess(LoginSuccessPacket loginSuccessPacket) {
        this.network_session.assignForeignUUID(loginSuccessPacket.getUUIDPayload());
        this.network_session.scheduleOutboundPacket(new LoginConfirmPacket(this.network_session.getEngineAssignedUUID(), this.network_session.getForeignUUID()), new GenericFutureListener[]{
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
            Validate.validState((connectionState == null && connectionState1 == ConnectionState.LOGIN) || (connectionState == ConnectionState.LOGIN && connectionState1 == ConnectionState.RUNTIME), "Cannot switch from connection state %s to %s", connectionState == null ? "NULL" : connectionState.toString(), connectionState1 == null ? "NULL" : connectionState1.toString());
        }

        catch (IllegalStateException ex) {
            this.engine.error("Invalid connection state transition", ex);
            this.network_session.shutdown(true);
        }

        if (connectionState1 == ConnectionState.RUNTIME) {
            this.network_session.setNetworkProtocol(new ClientRuntimeProtocol(this.engine, this.network_session));
        }
    }
}
