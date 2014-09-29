package jonathansmith.dpad.common.network.packet.play.plugin;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import jonathansmith.dpad.api.database.LoadingPluginRecord;
import jonathansmith.dpad.api.plugins.data.Dataset;

import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.server.network.protocol.ServerRuntimeNetworkProtocol;

/**
 * Created by Jon on 29/09/2014.
 */
public class DatasetPacket extends Packet {

    private Dataset dataset;

    public DatasetPacket() {
    }

    public DatasetPacket(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.dataset = new Dataset();
        this.dataset.setSampleName(packetBuffer.readStringFromBuffer(1024));
        this.dataset.setSampleMeasurementCondition(packetBuffer.readStringFromBuffer(1024));
        LoadingPluginRecord source = new LoadingPluginRecord();
        source.setPluginName(packetBuffer.readStringFromBuffer(1024));
        source.setPluginDescription(packetBuffer.readStringFromBuffer(1024));
        source.setPluginAuthor(packetBuffer.readStringFromBuffer(1024));
        source.setPluginOrganisation(packetBuffer.readStringFromBuffer(1024));
        this.dataset.setPluginSource(source);

        TreeMap<Integer, Integer> data = new TreeMap<Integer, Integer>();
        for (int i = 0; i < packetBuffer.readVarIntFromBuffer(); i++) {
            data.put(packetBuffer.readVarIntFromBuffer(), packetBuffer.readVarIntFromBuffer());
        }

        this.dataset.setData(data);
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeStringToBuffer(this.dataset.getSampleName());
        packetBuffer.writeStringToBuffer(this.dataset.getSampleMeasurementCondition());
        packetBuffer.writeStringToBuffer(this.dataset.getPluginSource().getPluginName());
        packetBuffer.writeStringToBuffer(this.dataset.getPluginSource().getPluginDescription());
        packetBuffer.writeStringToBuffer(this.dataset.getPluginSource().getPluginAuthor());
        packetBuffer.writeStringToBuffer(this.dataset.getPluginSource().getPluginOrganisation());

        TreeMap<Integer, Integer> data = this.dataset.getMeasurements();
        packetBuffer.writeVarIntToBuffer(data.size());
        for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
            packetBuffer.writeVarIntToBuffer(entry.getKey());
            packetBuffer.writeVarIntToBuffer(entry.getValue());
        }
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ServerRuntimeNetworkProtocol) networkProtocol).handleNewDataset(this);
    }

    @Override
    public String payloadToString() {
        return "New dataset packet for sample " + this.dataset.getSampleName();
    }

    public Dataset getDataset() {
        return this.dataset;
    }
}
