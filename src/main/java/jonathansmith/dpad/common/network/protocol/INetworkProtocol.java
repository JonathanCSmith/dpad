package jonathansmith.dpad.common.network.protocol;

import jonathansmith.dpad.common.network.ConnectionState;

/**
 * Created by Jon on 22/05/2014.
 * <p/>
 * Network protocol interface. Should be instanced by all network protocols.
 */
public interface INetworkProtocol {

    /**
     * Return the string instance of this protocol
     *
     * @return the protocol name
     */
    String getProtocolName();

    /**
     * Handle a transition between connection states.
     *
     * @param connectionState  current connection state
     * @param connectionState1 new connection state
     */
    void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1);

    /**
     * Generic method used by network managers to pulse the protocol for repeating tasks
     */
    void pulseScheduledProtocolTasks();

    /**
     * Responsible for handling an actual disconnect and any response that may occur. This should ONLY be called by the network manager.
     *
     * @param exitMessage message to display as the cause of disconnect
     */
    void onDisconnect(String exitMessage);
}
