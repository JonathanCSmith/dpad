package jonathansmith.jdpad.client.network.protocol;

import jonathansmith.jdpad.common.network.ConnectionState;
import jonathansmith.jdpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client login protocol. No encryption used...
 */
public class ClientLoginProtocol extends NetworkProtocol {

    private static final String PROTOCOL_NAME = "Client Login Protocol";

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {

    }

    @Override
    public void pulseRepeatPackets() {

    }

    @Override
    public void onDisconnect(String exitMessage) {

    }
}
