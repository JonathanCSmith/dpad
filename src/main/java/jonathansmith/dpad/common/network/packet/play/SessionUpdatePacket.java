package jonathansmith.dpad.common.network.packet.play;

import java.io.IOException;

import org.apache.commons.lang3.SerializationUtils;

import jonathansmith.dpad.api.database.Record;

import jonathansmith.dpad.common.network.SessionData;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.client.network.protocol.ClientRuntimeNetworkProtocol;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Session data update packet. keeps client and server in sync
 */
public class SessionUpdatePacket extends Packet {

    private SessionData.SessionDataType updateType;
    private Record                      updatePayload;

    public SessionUpdatePacket() {
    }

    public SessionUpdatePacket(SessionData.SessionDataType type, Record record) {
        this.updateType = type;
        this.updatePayload = record;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.updateType = SessionData.SessionDataType.values()[packetBuffer.readVarIntFromBuffer()];

        if (packetBuffer.readBoolean()) {
            this.updatePayload = SerializationUtils.deserialize(packetBuffer.readBlob());
        }
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeVarIntToBuffer(this.updateType.ordinal());

        if (this.updatePayload != null) {
            packetBuffer.writeBoolean(true);
            packetBuffer.writeBlob(SerializationUtils.serialize(this.updatePayload));
        }

        else {
            packetBuffer.writeBoolean(false);
        }
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ClientRuntimeNetworkProtocol) networkProtocol).handleSessionUpdate(this);
    }

    @Override
    public String payloadToString() {
        return "Session update for: " + this.updateType.toString();
    }

    public SessionData.SessionDataType getUpdateType() {
        return this.updateType;
    }

    public Record getUpdatePayload() {
        return this.updatePayload;
    }
}
