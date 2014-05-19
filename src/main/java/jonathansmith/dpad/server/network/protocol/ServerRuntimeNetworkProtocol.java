package jonathansmith.dpad.server.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * General runtime network protocol for the associated client
 * // TODO: Implement
 */
public class ServerRuntimeNetworkProtocol extends NetworkProtocol {

    private static final String PROTOCOL_NAME = "Server Runtime Protocol";

    public ServerRuntimeNetworkProtocol(IEngine engine, NetworkSession session) {
        super(engine, session);
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        this.engine.handleError("Unexpected protocol change. Attempted to change to" + connectionState1.toString(), new IllegalStateException("Protocol cannot be changed once runtime has been established"), false);
    }

    @Override
    public void pulseScheduledProtocolTasks() {
        // TODO: Keep alive
    }

    @Override
    public void onDisconnect(String exitMessage) {
        // TODO: Handle disconnect. Save session state etc exit message should become a class so we can determine reason?!
    }
}
