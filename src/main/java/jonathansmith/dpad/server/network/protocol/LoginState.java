package jonathansmith.dpad.server.network.protocol;

/**
 * Created by Jon on 07/04/14.
 * <p/>
 * Representation of the stages involved in the login process.
 */
public enum LoginState {

    GREETING,
    KEY_TRANSFER,
    READY_TO_FINISH, AUTHENTICATING, HANDSHAKE, ACCEPTING;
}
