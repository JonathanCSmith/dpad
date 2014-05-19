package jonathansmith.dpad.common.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Parent class for all network protocols
 */
public abstract class NetworkProtocol {

    protected final IEngine        engine;
    protected final NetworkSession networkSession;

    public NetworkProtocol(IEngine engine, NetworkSession session) {
        this.engine = engine;
        this.networkSession = session;
    }

    public abstract String getProtocolName();

    public abstract void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1);

    public abstract void pulseScheduledProtocolTasks();

    /**
     * Responsible for handling an actual disconnect and any response that may occur. This should ONLY be called by the network manager.
     *
     * @param exitMessage message to display as the cause of disconnect
     */
    public abstract void onDisconnect(String exitMessage);
}
