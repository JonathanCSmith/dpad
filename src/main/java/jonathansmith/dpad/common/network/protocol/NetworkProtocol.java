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

    public abstract void pulseRepeatPackets();

    public abstract void onDisconnect(String exitMessage);

    public void handleDisconnect(String reason) {
        this.networkSession.closeChannel(reason);
    }
}
