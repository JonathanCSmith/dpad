package jonathansmith.dpad.common.network.packet.handshake;

import java.io.IOException;

import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Passes the server side session information back to the client to confirm a successful login
 */
public class HandshakeSuccessPacket extends Packet {

    private String uuid;

    public HandshakeSuccessPacket() {
    }

    public HandshakeSuccessPacket(NetworkSession networkSession) {
        this.uuid = networkSession.getEngineAssignedUUID().toString();
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
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ClientLoginProtocol) networkProtocol).handleLoginSuccess(this);
    }

    @Override
    public String payloadToString() {
        return "Login success packet with uuid of: " + this.uuid;
    }
}
