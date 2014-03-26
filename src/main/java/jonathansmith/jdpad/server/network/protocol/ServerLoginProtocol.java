package jonathansmith.jdpad.server.network.protocol;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.network.ConnectionState;
import jonathansmith.jdpad.common.network.NetworkSession;
import jonathansmith.jdpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 */
public class ServerLoginProtocol extends NetworkProtocol {
    public ServerLoginProtocol(Engine engine, NetworkSession networkSession) {
        super();
    }

    @Override
    public String getProtocolName() {
        return null;
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
