package jonathansmith.dpad.common.network.packet;

import java.io.IOException;

import com.google.common.collect.BiMap;

import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Abstract packet class. Parent for all networking packets.
 */
public abstract class Packet {

    /**
     * Empty constructor to allow for packet registration without data
     */
    public Packet() {
    }

    /**
     * Generate an empty packet for automatic regeneration of the packet from the byte stream that is transmitted
     * through the packet pipeline
     *
     * @param integerClassBiMap the list of packets that are pre registered
     * @param packetId          the id of the packet within the packet list
     * @return the empty packet
     * @throws Exception if the packet could not be initialised
     */
    public static Packet getEmptyPacket(BiMap<Integer, Class<? extends Packet>> integerClassBiMap, int packetId) throws Exception {
        Class clazz = integerClassBiMap.get(packetId);
        return clazz == null ? null : (Packet) clazz.newInstance();
    }

    /**
     * Used to determine whether the packet needs to be processed immediately or should enter the packet queue.
     *
     * @return true if the packet should be processed out of order. Conventionally used for setup purposes only.
     * Return false if the packet should be processed normally.
     */
    public boolean isUrgent() {
        return false;
    }

    /**
     * Function called to generate the packet data from the byte stream
     *
     * @param packetBuffer the byte stream containing the packet's data
     * @throws IOException
     */
    public abstract void readPacketData(PacketBuffer packetBuffer) throws IOException;

    /**
     * Function to write the packet data into the bytestream using convenience methods in the {@link jonathansmith.dpad.common.network.packet.PacketBuffer}
     * class
     *
     * @param packetBuffer the packet buffer to write the data to
     * @throws IOException
     */
    public abstract void writePacketData(PacketBuffer packetBuffer) throws IOException;

    /**
     * Function called to handle the packet after it has been reconstituted from the byte stream
     *
     * @param networkProtocol a situational network protocol dependent on the state of the network connection.
     */
    public abstract void processPacket(INetworkProtocol networkProtocol);

    /**
     * Convenience method for debugging that converts the packet into a string
     *
     * @return summary of the packet
     */
    public abstract String payloadToString();
}
