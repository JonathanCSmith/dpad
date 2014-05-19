package jonathansmith.dpad.common.network.packet;

import java.io.IOException;

import jonathansmith.dpad.client.network.protocol.ClientNetworkProtocol;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Disconnect packet informing the client of a pending disconnect and the provided reason.
 */
public class DisconnectPacket extends Packet {

    private String reason;

    public DisconnectPacket() {
    }

    public DisconnectPacket(String s) {
        this.reason = s;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.reason = packetBuffer.readStringFromBuffer(32767);
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.reason);
    }

    @Override
    public void processPacket(NetworkProtocol networkProtocol) {
        ((ClientNetworkProtocol) networkProtocol).handleDisconnect(this.reason);
    }

    @Override
    public String payloadToString() {
        return "Disconnect packet with reason: " + this.reason;
    }
}
