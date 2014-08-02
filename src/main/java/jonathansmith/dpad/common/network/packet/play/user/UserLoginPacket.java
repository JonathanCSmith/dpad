package jonathansmith.dpad.common.network.packet.play.user;

import java.io.IOException;

import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.server.network.protocol.ServerRuntimeNetworkProtocol;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * User login packet. Login can either be for an existing or a new user.
 */
public class UserLoginPacket extends Packet {

    private boolean isNewUser;
    private String  userName;
    private String  password;

    public UserLoginPacket() {
    }

    public UserLoginPacket(boolean isNewUser, String userName, String password) {
        this.isNewUser = isNewUser;
        this.userName = userName;
        this.password = password;
    }

    public boolean isNewUser() {
        return this.isNewUser;
    }

    public String getUsername() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.isNewUser = packetBuffer.readBoolean();
        this.userName = packetBuffer.readStringFromBuffer(32);
        this.password = packetBuffer.readStringFromBuffer(32);
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeBoolean(this.isNewUser);
        packetBuffer.writeStringToBuffer(this.userName);
        packetBuffer.writeStringToBuffer(this.password);
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ServerRuntimeNetworkProtocol) networkProtocol).handleUserLogin(this);
    }

    @Override
    public String payloadToString() {
        return "User login packet. Is new user: " + this.isNewUser;
    }
}
