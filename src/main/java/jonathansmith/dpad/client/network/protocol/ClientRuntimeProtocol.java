package jonathansmith.dpad.client.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Client runtime protocol
 */
public class ClientRuntimeProtocol extends NetworkProtocol {

    public ClientRuntimeProtocol(IEngine engine, NetworkSession session) {
        super(engine, session);
    }

    @Override
    public String getProtocolName() {
        return null;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {

    }

    @Override
    public void pulseScheduledProtocolTasks() {

    }

    @Override
    public void onDisconnect(String exitMessage) {

    }
}
