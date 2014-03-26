package jonathansmith.jdpad.common.network.protocol;

import jonathansmith.jdpad.common.network.ConnectionState;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Parent class for all network protocols
 */
public abstract class NetworkProtocol {

    public abstract String getProtocolName();

    public abstract void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1);

    public abstract void pulseRepeatPackets();

    public abstract void onDisconnect(String exitMessage);
}
