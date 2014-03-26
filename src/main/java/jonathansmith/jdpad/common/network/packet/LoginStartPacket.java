package jonathansmith.jdpad.common.network.packet;

import java.io.IOException;

import jonathansmith.jdpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 *
 * Initial login data
 */
public class LoginStartPacket extends Packet{
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
