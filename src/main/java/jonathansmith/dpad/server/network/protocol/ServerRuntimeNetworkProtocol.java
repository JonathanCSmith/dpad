package jonathansmith.dpad.server.network.protocol;

import io.netty.util.concurrent.GenericFutureListener;

import org.apache.commons.lang3.Validate;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.play.KeepAlivePacket;
import jonathansmith.dpad.common.network.packet.play.user.UserChangePasswordPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserLoginPacket;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.network.ServerNetworkSession;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * General runtime network protocol for the associated client
 */
public class ServerRuntimeNetworkProtocol implements IRuntimeNetworkProtocol {

    private static final String PROTOCOL_NAME    = "Server Runtime Protocol";
    private static final long   KEEP_ALIVE_DELAY = 300L;

    private final ServerEngine   engine;
    private final NetworkSession network_session;

    private boolean sendKeepAlives    = false;
    private long    lastKeepAliveTime = 0L;

    public ServerRuntimeNetworkProtocol(ServerEngine engine, NetworkSession session) {
        this.engine = engine;
        this.network_session = session;
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        try {
            Validate.validState(connectionState1 == ConnectionState.RUNTIME, "Unexpected protocol change. Attempted to change to %s from %s", connectionState1.toString(), connectionState.toString());
        }

        catch (IllegalStateException ex) {
            this.engine.error("Invalid connection state transition", ex);
        }
    }

    @Override
    public void pulseScheduledProtocolTasks() {
        if (!this.sendKeepAlives) {
            return;
        }

        if (this.lastKeepAliveTime == 0L) {
            this.lastKeepAliveTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - this.lastKeepAliveTime > KEEP_ALIVE_DELAY) {
            this.lastKeepAliveTime = System.currentTimeMillis();
            this.network_session.scheduleOutboundPacket(new KeepAlivePacket(), new GenericFutureListener[0]);
        }
    }

    @Override
    public void onDisconnect(String exitMessage) {
        this.engine.info(this.network_session.buildSessionInformation() + " lost connection: " + exitMessage, null);
    }

    @Override
    public void handleKeepAlive() {
        // Do nothing
    }

    public void handleLoginConfirmation() {
        this.sendKeepAlives = true;
    }

    public void handleUserLogin(UserLoginPacket userLoginPacket) {
        if (this.network_session.getSessionData().isUserLoggedIn()) {
            // How the fuck?!
            // MAYBE: RE-SYNC as opposed to failure?
            this.engine.handleError("Something is really wrong with the session data. Currently the user: " + this.network_session.getSessionData().getUserName() + " is attempting to login as: " + userLoginPacket.getUsername(), new RuntimeException());
            return;
        }

        ((ServerNetworkSession) this.network_session).handleUserLogin(userLoginPacket.isNewUser(), userLoginPacket.getUsername(), userLoginPacket.getPassword());
    }

    public void handleUserLogout() {
        if (!this.network_session.getSessionData().isUserLoggedIn()) {
            // How the fuck?!
            // MAYBE: RE-SYNC as opposed to failure?
            this.engine.handleError("Something is really wrong with the session data. The session: " + this.network_session.getEngineAssignedUUID() + " is attempting to logout without being logged in!", new RuntimeException());
            return;
        }

        ((ServerNetworkSession) this.network_session).handleUserLogout();
    }

    public void handleUserChangePassword(UserChangePasswordPacket userChangePasswordPacket) {
        if (!this.network_session.getSessionData().isUserLoggedIn()) {
            // How the fuck?!
            // MAYBE: RE-SYNC as opposed to failure?
            this.engine.handleError("Something is really wrong with the session data. The session: " + this.network_session.getEngineAssignedUUID() + " is attempting to change it's password without being logged in!", new RuntimeException());
            return;
        }

        ((ServerNetworkSession) this.network_session).handleUserChangePassword(userChangePasswordPacket.getOldPassword(), userChangePasswordPacket.getNewPassword());
    }
}
