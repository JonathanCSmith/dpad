package jonathansmith.dpad.common.network.packet.login;

import java.io.IOException;

import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;
import jonathansmith.dpad.server.network.protocol.ServerLoginNetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Initial login data
 */
public class LoginStartPacket extends Packet {

    private String uuid;

    public LoginStartPacket(NetworkSession session) {
        this.uuid = session.getEngineAssignedUUID();
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.uuid = packetBuffer.readStringFromBuffer(16);
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.uuid);
    }

    @Override
    public void processPacket(NetworkProtocol networkProtocol) {
        ((ServerLoginNetworkProtocol) networkProtocol).handleLoginStart(this.uuid);
    }

    @Override
    public String payloadToString() {
        return "Login start packet with a foreign assigned id of: " + this.uuid;
    }
}
