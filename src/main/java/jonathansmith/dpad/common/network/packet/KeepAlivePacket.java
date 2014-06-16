package jonathansmith.dpad.common.network.packet;

import java.io.IOException;

import jonathansmith.dpad.common.network.protocol.INetworkProtocol;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

/**
 * Created by Jon on 22/05/2014.
 * <p/>
 * Keep alive packet. Used by the server to keep channels open.
 */
public class KeepAlivePacket extends Packet {

    private static final String TO_STRING_INFO = "Keep Alive!";

    public KeepAlivePacket() {
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((IRuntimeNetworkProtocol) networkProtocol).handleKeepAlive();
    }

    @Override
    public String payloadToString() {
        return TO_STRING_INFO;
    }
}
