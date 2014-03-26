package jonathansmith.jdpad.common.network.packet;

import java.io.IOException;

import jonathansmith.jdpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 *
 * Disconnect packet informing the client of a pending disconnect.
 */
public class DisconnectPacket extends Packet {

    public DisconnectPacket() {}

    public DisconnectPacket(String s) {
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void processPacket(NetworkProtocol networkProtocol) {

    }

    @Override
    public String payloadToString() {
        return null;
    }
}
