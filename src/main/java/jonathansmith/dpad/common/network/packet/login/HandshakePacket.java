package jonathansmith.dpad.common.network.packet.login;

import java.io.IOException;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;
import jonathansmith.dpad.server.network.protocol.ServerLoginNetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Handshake packet responsible for initialising client server connections
 */
public class HandshakePacket extends Packet {

    private String          version;
    private String          socketAddress;
    private int             port;
    private ConnectionState connectionState;

    public HandshakePacket() {
    }

    public HandshakePacket(String version, String socket, int port, ConnectionState state) {
        this.version = version;
        this.socketAddress = socket;
        this.port = port;
        this.connectionState = state;
    }

    public String getNetworkProtocolVersion() {
        return this.version;
    }

    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    @Override
    public boolean isUrgent() {
        return true;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.version = packetBuffer.readStringFromBuffer(255);
        this.socketAddress = packetBuffer.readStringFromBuffer(255);
        this.port = packetBuffer.readUnsignedShort();
        this.connectionState = ConnectionState.getConnectionStateFromStateFlag(packetBuffer.readVarIntFromBuffer());
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.version);
        packetBuffer.writeStringToBuffer(this.socketAddress);
        packetBuffer.writeShort(this.port);
        packetBuffer.writeVarIntToBuffer(this.connectionState.getStateFlag());
    }

    @Override
    public void processPacket(NetworkProtocol networkProtocol) {
        ((ServerLoginNetworkProtocol) networkProtocol).handleHandshake(this);
    }

    @Override
    public String payloadToString() {
        return "Version: " + this.version + ", SocektAddress: " + this.socketAddress + ", Port: " + this.port + ", ConnectionState: " + this.connectionState;
    }
}
