package jonathansmith.dpad.common.network.packet.play.dataset;

import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.lang3.SerializationUtils;

import jonathansmith.dpad.common.engine.state.DatasetAdministationState;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

/**
 * Created by Jon on 29/09/2014.
 */
public class DatasetAdministrationPacket extends Packet {

    private DatasetAdministationState state;
    private HashSet                   interestedRecords;

    public DatasetAdministrationPacket() {
    }

    public DatasetAdministrationPacket(DatasetAdministationState state, HashSet interestedRecords) {
        this.state = state;
        this.interestedRecords = interestedRecords;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.state = DatasetAdministationState.values()[packetBuffer.readVarIntFromBuffer()];

        if (packetBuffer.readBoolean()) {
            this.interestedRecords = SerializationUtils.deserialize(packetBuffer.readBlob());
        }
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeVarIntToBuffer(this.state.ordinal());

        if (this.interestedRecords != null) {
            packetBuffer.writeBoolean(true);
            packetBuffer.writeBlob(SerializationUtils.serialize(this.interestedRecords));
        }

        else {
            packetBuffer.writeBoolean(false);
        }
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((IRuntimeNetworkProtocol) networkProtocol).handleDatasetAdministrationPacket(this);
    }

    @Override
    public String payloadToString() {
        return "Dataset administration packet with state: " + this.state.toString();
    }

    public DatasetAdministationState getPacketState() {
        return this.state;
    }

    public HashSet getInterestedRecords() {
        return this.interestedRecords;
    }
}
