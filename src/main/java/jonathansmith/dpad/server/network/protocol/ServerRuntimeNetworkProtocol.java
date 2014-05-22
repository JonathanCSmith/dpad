package jonathansmith.dpad.server.network.protocol;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.KeepAlivePacket;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

import org.apache.commons.lang3.Validate;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * General runtime network protocol for the associated client
 * // TODO: Implement
 */
public class ServerRuntimeNetworkProtocol implements IRuntimeNetworkProtocol {

    private static final String PROTOCOL_NAME = "Server Runtime Protocol";

    private final IEngine        engine;
    private final NetworkSession network_session;

    private boolean sendKeepAlives    = false;
    private long    lastKeepAliveTime = 0L;

    public ServerRuntimeNetworkProtocol(IEngine engine, NetworkSession session) {
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

        if (System.currentTimeMillis() - this.lastKeepAliveTime < 30L) {
            this.lastKeepAliveTime = System.currentTimeMillis();
            this.network_session.scheduleOutboundPacket(new KeepAlivePacket(), new GenericFutureListener[0]);
        }
    }

    @Override
    public void onDisconnect(String exitMessage) {
        this.engine.info(this.network_session.buildSessionInformation() + " lost connection: " + exitMessage, null);
        // TODO: get Session, sync pendings
        // TODO: Close session
        // TODO: Close channel
        // TODO: if local, we shutdown the server?!
    }

    @Override
    public void handleKeepAlive() {
        // Do nothing
    }

    public void handleLoginConfirmation() {
        this.sendKeepAlives = true;
    }
}
