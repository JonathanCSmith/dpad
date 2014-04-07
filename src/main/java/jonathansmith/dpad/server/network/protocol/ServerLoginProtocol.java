package jonathansmith.dpad.server.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 */
public class ServerLoginProtocol extends NetworkProtocol {

    public ServerLoginProtocol(IEngine engine, NetworkSession networkSession) {
        super(engine, networkSession);
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
