package jonathansmith.dpad.common.network.packet;

import java.io.IOException;

import com.google.common.collect.BiMap;

import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Abstract packet class. Parent for all networking packets.
 */
public abstract class Packet {

    public Packet() {
    }

    public static Packet getEmptyPacket(BiMap<Integer, Class<? extends Packet>> integerClassBiMap, int packetId) throws Exception {
        Class clazz = integerClassBiMap.get(packetId);
        return clazz == null ? null : (Packet) clazz.newInstance();
    }

    public boolean isUrgent() {
        return false;
    }

    public abstract void readPacketData(PacketBuffer packetBuffer) throws IOException;

    public abstract void writePacketData(PacketBuffer packetBuffer) throws IOException;

    public abstract void processPacket(NetworkProtocol networkProtocol);

    public abstract String payloadToString();
}
