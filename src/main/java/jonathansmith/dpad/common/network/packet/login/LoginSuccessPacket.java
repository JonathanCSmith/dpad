package jonathansmith.dpad.common.network.packet.login;

import java.io.IOException;

import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Passes the server side session information back to the client to confirm a successful login
 */
public class LoginSuccessPacket extends Packet {

    private String uuid;

    public LoginSuccessPacket() {
    }

    public LoginSuccessPacket(NetworkSession networkSession) {
        this.uuid = networkSession.getEngineAssignedUUID();
    }

    public String getUUIDPayload() {
        return this.uuid;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.uuid = packetBuffer.readStringFromBuffer(36);
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.uuid);
    }

    @Override
    public void processPacket(NetworkProtocol networkProtocol) {
        ((ClientLoginProtocol) networkProtocol).handleLoginSuccess(this);
    }

    @Override
    public String payloadToString() {
        return "Login success packet with uuid of: " + this.uuid;
    }
}
