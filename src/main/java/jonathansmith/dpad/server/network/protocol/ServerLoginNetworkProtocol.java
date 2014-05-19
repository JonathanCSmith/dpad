package jonathansmith.dpad.server.network.protocol;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKey;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.DisconnectPacket;
import jonathansmith.dpad.common.network.packet.login.EncryptionRequestPacket;
import jonathansmith.dpad.common.network.packet.login.EncryptionResponsePacket;
import jonathansmith.dpad.common.network.packet.login.LoginStartPacket;
import jonathansmith.dpad.common.network.packet.login.LoginSuccessPacket;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;
import jonathansmith.dpad.server.engine.util.version.Version;
import org.apache.commons.lang3.Validate;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Protocol used by the network during the login process (Server side). Uses an unencrypted channel until keys are shared.
 */
public class ServerLoginNetworkProtocol extends NetworkProtocol {

    private static final String PROTOCOL_NAME       = "Server login protocol";
    private static final Random LOGIN_KEY_GENERATOR = new Random();
    private static final int    LOGIN_TIMEOUT       = 600;

    private final boolean isLocalConnection;
    private final byte[] loginKey = new byte[4];

    private LoginState loginState = LoginState.GREETING;
    private SecretKey secretKey;
    private int       loginTime;

    public ServerLoginNetworkProtocol(IEngine engine, NetworkSession networkSession, boolean isLocalConnection) {
        super(engine, networkSession);
        this.isLocalConnection = isLocalConnection;
        LOGIN_KEY_GENERATOR.nextBytes(this.loginKey);
    }

    // Begins the login process by disseminating a shared key to the client for encryption
    public void handleLoginStart(LoginStartPacket packet) {
        Validate.validState(this.loginState == LoginState.GREETING, "Unexpected login start packet during the: " + this.loginState + " state");

        boolean versionMatch = false;
        if (this.isLocalConnection) {
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

        this.networkSession.assignForeignUUID(packet.getUUID());

        if (this.isLocalConnection) {
            this.loginState = LoginState.READY_TO_FINISH;
        }

        else {
            this.loginState = LoginState.KEY_TRANSFER;
            this.networkSession.scheduleOutboundPacket(new EncryptionRequestPacket(this.engine.getNetworkManager().getKeyPair().getPublic(), this.loginKey), new GenericFutureListener[0]);
        }
    }

    // Finalises the login process by completing the key exchange and enabling encryption on the channel
    public void handleEncryptionResponse(EncryptionResponsePacket encryptionResponsePacket) {
        Validate.validState(this.loginState == LoginState.KEY_TRANSFER, "Unexpected encryption response packet during the: " + this.loginState + " state");
        PrivateKey privateKey = this.engine.getNetworkManager().getKeyPair().getPrivate();

        if (!Arrays.equals(this.loginKey, encryptionResponsePacket.decodeRandomSignature(privateKey))) {
            this.handleLoginFailure("Invalid login sequence");
        }

        else {
            this.secretKey = encryptionResponsePacket.decodeSecretKey(privateKey);
            this.loginState = LoginState.AUTHENTICATING;
            this.networkSession.enableEncryption(this.secretKey);
            this.loginState = LoginState.READY_TO_FINISH;
        }
    }

    // Starts the transition process between logging in + general runtime
    private void handleLoginFinish() {
        String joinMsg = this.engine.getNetworkManager().allowUserToConnect(this.networkSession);
        if (joinMsg != null) {
            this.handleLoginFailure(joinMsg);
        }

        else {
            this.loginState = LoginState.ACCEPTING;
            this.networkSession.scheduleOutboundPacket(new LoginSuccessPacket(this.networkSession), new GenericFutureListener[0]);
            this.networkSession.finaliseConnection();
        }
    }

    private void handleLoginFailure(String reason) {
        this.engine.handleError("Could not accept client due to: " + reason, null, false);
        this.networkSession.scheduleOutboundPacket(new DisconnectPacket(reason), new GenericFutureListener[0]);
        this.networkSession.closeChannel(reason);
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        Validate.validState(this.loginState == LoginState.ACCEPTING, "Unexpected connection state transition when login is not yet complete");
        Validate.validState(connectionState1 == ConnectionState.RUNTIME || connectionState1 == ConnectionState.LOGIN, "Unexpected connection state");
    }

    @Override
    public void pulseScheduledProtocolTasks() {
        if (this.loginState == LoginState.READY_TO_FINISH) {
            this.handleLoginFinish();
        }

        if (this.loginTime++ == LOGIN_TIMEOUT) {
            this.handleLoginFailure("Took too long to log in!");
        }
    }

    @Override
    public void onDisconnect(String exitMessage) {
        this.engine.error(this.networkSession.buildSessionInformation() + " lost connection: " + exitMessage, null);
    }
}
