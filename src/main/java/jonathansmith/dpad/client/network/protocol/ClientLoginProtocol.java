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
import jonathansmith.dpad.common.network.packet.login.LoginSuccessPacket;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client login protocol. No encryption used...
 */
public class ClientLoginProtocol extends ClientNetworkProtocol {

    private static final String PROTOCOL_NAME = "Client Login Protocol";

    public ClientLoginProtocol(IEngine engine, NetworkSession session) {
        super(engine, session);
    }

    public void handleEncryptionRequest(PublicKey key, byte[] randomSignature) {
        final SecretKey secretKey = CryptographyManager.createNewSharedKey();
        this.networkSession.scheduleOutboundPacket(new EncryptionResponsePacket(secretKey, key, randomSignature), new GenericFutureListener[]{
                new GenericFutureListener() {
                    @Override
                    public void operationComplete(Future future) throws Exception {
                        ClientLoginProtocol.this.networkSession.enableEncryption(secretKey);
                    }
                }
        });
    }

    public void handleLoginSuccess(LoginSuccessPacket loginSuccessPacket) {
        this.networkSession.setConnectionState(ConnectionState.RUNTIME);
        this.networkSession.assignForeignUUID(loginSuccessPacket.getUUIDPayload());
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        this.engine.debug("Switching from: " + connectionState.toString() + " to: " + connectionState1.toString(), null);

        if (connectionState1 == ConnectionState.RUNTIME) {
            this.networkSession.setNetworkProtocol(new ClientRuntimeProtocol(this.engine, this.networkSession));
        }
    }

    @Override
    public void pulseScheduledProtocolTasks() {
    }

    @Override
    public void handleDisconnect(String reason) {
        this.networkSession.closeChannel(reason);
    }

    @Override
    public void onDisconnect(String exitMessage) {
        // TODO: Cleanup
        // TODO: Disconnect message
    }
}
