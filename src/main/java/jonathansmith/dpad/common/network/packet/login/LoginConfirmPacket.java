package jonathansmith.dpad.common.network.packet.login;

import java.io.IOException;

import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.server.network.protocol.ServerRuntimeNetworkProtocol;

/**
 * Created by Jon on 22/05/2014.
 * <p/>
 * Login confirmation packet. Begins the keep alive packet communiques
 */
public class LoginConfirmPacket extends Packet {

    private String clientUUID;
    private String serverUUID;

    public LoginConfirmPacket() {
    }

    public LoginConfirmPacket(String clientUUID, String serverUUID) {
        this.clientUUID = clientUUID;
        this.serverUUID = serverUUID;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.clientUUID = packetBuffer.readStringFromBuffer(36);
        this.serverUUID = packetBuffer.readStringFromBuffer(36);
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.clientUUID);
        packetBuffer.writeStringToBuffer(this.serverUUID);
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ServerRuntimeNetworkProtocol) networkProtocol).handleLoginConfirmation();
    }

    @Override
    public String payloadToString() {
        return null;
    }
}
