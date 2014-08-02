package jonathansmith.dpad.common.network.packet.play.user;

import java.io.IOException;

import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.server.network.protocol.ServerRuntimeNetworkProtocol;

/**
 * Created by Jon on 02/08/2014.
 * <p/>
 * User change password packet
 */
public class UserChangePasswordPacket extends Packet {

    private String oldPassword;
    private String newPassword;

    public UserChangePasswordPacket() {
    }

    public UserChangePasswordPacket(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.oldPassword = packetBuffer.readStringFromBuffer(32);
        this.newPassword = packetBuffer.readStringFromBuffer(32);
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.oldPassword);
        packetBuffer.writeStringToBuffer(this.newPassword);
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ServerRuntimeNetworkProtocol) networkProtocol).handleUserChangePassword(this);
    }

    @Override
    public String payloadToString() {
        return "User change password packet. Payload masked.";
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }
}
