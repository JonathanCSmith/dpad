package jonathansmith.dpad.common.network.packet.play.user;

import java.io.IOException;

import jonathansmith.dpad.common.engine.user.UserResponseState;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.client.network.protocol.ClientRuntimeNetworkProtocol;

/**
 * Created by Jon on 24/07/2014.
 * <p/>
 * Response packet to all possible user login states.
 */
public class UserAdministrationResponsePacket extends Packet {

    private UserResponseState state;

    public UserAdministrationResponsePacket() {
    }

    public UserAdministrationResponsePacket(UserResponseState state) {
        this.state = state;
    }

    public UserResponseState getState() {
        return this.state;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.state = UserResponseState.values()[packetBuffer.readVarIntFromBuffer()];
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeVarIntToBuffer(this.state.ordinal());
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ClientRuntimeNetworkProtocol) networkProtocol).handleUserAdministrationResponse(this);
    }

    @Override
    public String payloadToString() {
        return null;
    }
}
