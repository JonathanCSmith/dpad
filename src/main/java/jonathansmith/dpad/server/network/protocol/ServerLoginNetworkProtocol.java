package jonathansmith.dpad.server.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Protocol used by the network during the login process (Server side). Uses an unencrypted channel until keys are shared.
 */
public class ServerLoginNetworkProtocol extends NetworkProtocol {

    public ServerLoginNetworkProtocol(IEngine engine, NetworkSession networkSession) {
        super(engine, networkSession);
    }

    public void handleLoginStart(String foreignUUID) {
        this.networkSession.assignForeignUUID(foreignUUID);
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
