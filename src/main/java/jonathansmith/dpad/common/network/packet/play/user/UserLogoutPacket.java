package jonathansmith.dpad.common.network.packet.play.user;

import java.io.IOException;

import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.server.network.protocol.ServerRuntimeNetworkProtocol;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * User logout packet. Sent from both sides to indicate initiation and conformation on client and server respectively
 */
public class UserLogoutPacket extends Packet {

    public UserLogoutPacket() {
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ServerRuntimeNetworkProtocol) networkProtocol).handleUserLogout();
    }

    @Override
    public String payloadToString() {
        return "User logout packet";
    }
}
