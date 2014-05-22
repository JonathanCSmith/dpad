package jonathansmith.dpad.server.network.protocol;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKey;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.login.*;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.server.engine.util.version.Version;
import jonathansmith.dpad.server.network.ServerNetworkSession;

import org.apache.commons.lang3.Validate;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Protocol used by the network during the login process (Server side). Uses an unencrypted channel until keys are shared.
 */
public class ServerLoginNetworkProtocol implements INetworkProtocol {

    private static final String PROTOCOL_NAME       = "Server login protocol";
    private static final Random LOGIN_KEY_GENERATOR = new Random();
    private static final long   LOGIN_TIMEOUT       = 30000;

    private final IEngine        engine;
    private final NetworkSession network_session;
    private final boolean        is_local_connection;
    private final byte[] login_key = new byte[4];

    private long loginTime;

    private LoginState loginState = LoginState.GREETING;
    private SecretKey secretKey;

    public ServerLoginNetworkProtocol(IEngine engine, NetworkSession networkSession, boolean isLocalConnection) {
        this.engine = engine;
        this.network_session = networkSession;
        this.is_local_connection = isLocalConnection;
        LOGIN_KEY_GENERATOR.nextBytes(this.login_key);
    }

    // Begins the login process by disseminating a shared key to the client for encryption
    public void handleLoginStart(LoginStartPacket packet) {
        Validate.validState(this.loginState == LoginState.GREETING, "Unexpected login start packet during the: " + this.loginState + " state");
        this.loginTime = System.currentTimeMillis();

        boolean versionMatch = false;
        if (this.is_local_connection) {
            versionMatch = true;
        }

        else {
            if (Version.isCompatible(this.engine.getVersion(), packet.getVersion())) {
                versionMatch = true;
            }
        }

        if (!versionMatch) {
            this.handleLoginFailure("Network protocol version mismatch");
            return;
        }

        this.network_session.assignForeignUUID(packet.getUUID());

        if (this.is_local_connection) {
            this.loginState = LoginState.READY_TO_FINISH;
        }

        else {
            this.loginState = LoginState.KEY_TRANSFER;
            this.network_session.scheduleOutboundPacket(new EncryptionRequestPacket(((ServerNetworkSession) this.network_session).getNetworkManager().getKeyPair().getPublic(), this.login_key), new GenericFutureListener[0]);
        }
    }

    // Finalises the login process by completing the key exchange and enabling encryption on the channel
    public void handleEncryptionResponse(EncryptionResponsePacket encryptionResponsePacket) {
        Validate.validState(this.loginState == LoginState.KEY_TRANSFER, "Unexpected encryption response packet during the: " + this.loginState + " state");
        PrivateKey privateKey = ((ServerNetworkSession) this.network_session).getNetworkManager().getKeyPair().getPrivate();

        if (!Arrays.equals(this.login_key, encryptionResponsePacket.decodeRandomSignature(privateKey))) {
            this.handleLoginFailure("Invalid login sequence");
        }

        else {
            this.secretKey = encryptionResponsePacket.decodeSecretKey(privateKey);
            this.loginState = LoginState.AUTHENTICATING;
            this.network_session.enableEncryption(this.secretKey);
            this.loginState = LoginState.READY_TO_FINISH;
        }
    }

    // Starts the transition process between logging in + general runtime
    private void handleLoginFinish() {
        String joinMsg = ((ServerNetworkSession) this.network_session).getNetworkManager().allowUserToConnect(this.network_session);
        if (joinMsg != null) {
            this.handleLoginFailure(joinMsg);
        }

        else {
            this.loginState = LoginState.ACCEPTING;
            this.network_session.scheduleOutboundPacket(new LoginSuccessPacket(this.network_session), new GenericFutureListener[0]);
            ((ServerNetworkSession) this.network_session).finaliseConnection();
        }
    }

    private void handleLoginFailure(String reason) {
        this.engine.error("Could not accept client due to: " + reason, null);
        this.network_session.scheduleOutboundPacket(new LoginDisconnectPacket(reason), new GenericFutureListener[0]);
        this.network_session.closeChannel(reason);
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        try {
            Validate.validState(this.loginState == LoginState.ACCEPTING, "Unexpected connection state transition when login is not yet complete");
            Validate.validState(connectionState == ConnectionState.LOGIN && connectionState1 == ConnectionState.RUNTIME, "Cannot switch from connection state %s to %s", connectionState == null ? "NULL" : connectionState.toString(), connectionState1 == null ? "NULL" : connectionState1.toString());
        }

        catch (IllegalStateException ex) {
            this.engine.error("Invalid connection state transition", ex);
        }
    }

    @Override
    public void pulseScheduledProtocolTasks() {
        if (this.loginState == LoginState.READY_TO_FINISH) {
            this.handleLoginFinish();
        }

        if (this.network_session.isChannelOpen() && System.currentTimeMillis() - this.loginTime == LOGIN_TIMEOUT) {
            this.handleLoginFailure("Took too long to log in!");
        }
    }

    @Override
    public void onDisconnect(String exitMessage) {
        this.engine.info(this.network_session.buildSessionInformation() + " lost connection: " + exitMessage, null);
    }
}
