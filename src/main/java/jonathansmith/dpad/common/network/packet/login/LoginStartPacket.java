package jonathansmith.dpad.common.network.packet.login;

import java.io.IOException;

import jonathansmith.dpad.api.common.util.Version;

import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.server.network.protocol.ServerLoginNetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Initial login data
 */
public class LoginStartPacket extends Packet {

    private Version version;
    private String  uuid;
    private String  address;
    private int     port;

    public LoginStartPacket() {
    }

    public LoginStartPacket(Version version, NetworkSession session) {
        this.version = version;
        this.uuid = session.getEngineAssignedUUID();
        this.address = session.getAddress();
        this.port = Integer.parseInt(session.getPort());
    }

    public Version getVersion() {
        return this.version;
    }

    public String getUUID() {
        return this.uuid;
    }

    @Override
    public boolean isUrgent() {
        return true;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.version = new Version(packetBuffer.readStringFromBuffer(255));
        this.uuid = packetBuffer.readStringFromBuffer(36);
        this.address = packetBuffer.readStringFromBuffer(255);
        this.port = packetBuffer.readUnsignedShort();
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.version.getVersionString());
        packetBuffer.writeStringToBuffer(this.uuid);
        packetBuffer.writeStringToBuffer(this.address);
        packetBuffer.writeShort(this.port);
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ServerLoginNetworkProtocol) networkProtocol).handleLoginStart(this);
    }

    @Override
    public String payloadToString() {
        return String.format("Login start packet with version: %s, client uuid: %s, socket address: %s and port: %d", this.version, this.uuid, this.address, this.port);
    }
}
