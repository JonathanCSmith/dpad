package jonathansmith.dpad.common.network.packet.play.experiment;

import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.lang3.SerializationUtils;

import jonathansmith.dpad.api.database.ExperimentRecord;

import jonathansmith.dpad.common.engine.state.ExperimentAdministrationState;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Packet handling experiment record administration
 */
public class ExperimentAdministrationPacket extends Packet {

    private ExperimentAdministrationState administrationState;
    private ExperimentRecord              record;
    private HashSet<ExperimentRecord>     experiments;

    public ExperimentAdministrationPacket() {
    }

    public ExperimentAdministrationPacket(ExperimentAdministrationState administrationState) {
        this.administrationState = administrationState;
    }

    public ExperimentAdministrationPacket(ExperimentAdministrationState administrationState, ExperimentRecord record) {
        this(administrationState);

        this.record = record;
    }

    public ExperimentAdministrationPacket(ExperimentAdministrationState administrationState, HashSet<ExperimentRecord> currentExperiments) {
        this(administrationState);

        this.experiments = currentExperiments;
    }

    public ExperimentAdministrationState getAdministrationState() {
        return this.administrationState;
    }

    public ExperimentRecord getExperimentRecord() {
        return this.record;
    }

    public HashSet<ExperimentRecord> getExperiments() {
        return this.experiments;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.administrationState = ExperimentAdministrationState.values()[packetBuffer.readVarIntFromBuffer()];

        if (packetBuffer.readBoolean()) {
            this.record = SerializationUtils.deserialize(packetBuffer.readBlob());
        }

        if (packetBuffer.readBoolean()) {
            this.experiments = SerializationUtils.deserialize(packetBuffer.readBlob());
        }
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeVarIntToBuffer(this.administrationState.ordinal());

        if (this.record != null) {
            packetBuffer.writeBoolean(true);
            packetBuffer.writeBlob(SerializationUtils.serialize(this.record));
        }

        else {
            packetBuffer.writeBoolean(false);
        }

        if (this.experiments != null) {
            packetBuffer.writeBoolean(true);
            packetBuffer.writeBlob(SerializationUtils.serialize(this.experiments));
        }

        else {
            packetBuffer.writeBoolean(false);
        }
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        if (networkProtocol instanceof IRuntimeNetworkProtocol) {
            ((IRuntimeNetworkProtocol) networkProtocol).handleExperimentAdministration(this);
        }
    }

    @Override
    public String payloadToString() {
        return "Experiment Administration Packet with state: " + this.administrationState.toString();
    }
}
