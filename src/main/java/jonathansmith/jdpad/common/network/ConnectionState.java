package jonathansmith.jdpad.common.network;

import java.util.Iterator;
import java.util.Map;

import com.beust.jcommander.internal.Maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

import jonathansmith.jdpad.common.network.packet.Packet;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import jonathansmith.jdpad.JDPAD;
import org.dom4j.IllegalAddException;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Enum representing all possible connection states between the client and server. Packets are whitelisted to prevent unauthorised communications.
 */
public enum ConnectionState {

    HANDSHAKING(-1),
    PLAY(0),
    LOGIN(1);

    private final int                                     stateFlag;
    private final BiMap<Integer, Class<? extends Packet>> whitelistedClientPackets;
    private final BiMap<Integer, Class<? extends Packet>> whitelistedServerPackets;

    private ConnectionState(int stateFlag) {
        this.stateFlag = stateFlag;
        this.whitelistedClientPackets = HashBiMap.create();
        this.whitelistedServerPackets = HashBiMap.create();
    }

    protected void addClientPacket(Class<? extends Packet> clazz) throws IllegalAddException {
        if (this.addPacket(clazz, this.whitelistedClientPackets)) {
            String error = "Failure to register client packet: " + clazz.getCanonicalName();
            JDPAD.getInstance().handleError(error, null, true);
            throw new IllegalAddException(error);
        }
    }

    protected void addServerPacket(Class<? extends Packet> clazz) throws IllegalAddException {
        if (!this.addPacket(clazz, this.whitelistedServerPackets)) {
            String error = "Failure to register client packet: " + clazz.getCanonicalName();
            JDPAD.getInstance().handleError(error, null, true);
            throw new IllegalAddException(error);
        }
    }

    private boolean addPacket(Class<? extends Packet> clazz, BiMap map) {
        if (map.containsValue(clazz)) {
            return false;
        }

        int id = map.size();
        map.put(id, clazz);
        return true;
    }

    public BiMap<Integer, Class<? extends Packet>> getSideSpecificWhitelistedPackets(boolean isClientSide) {
        return isClientSide ? this.getClientSideWhitelistedPackets() : this.getServerSideWhitelistedPackets();
    }

    public BiMap<Integer, Class<? extends Packet>> getClientSideWhitelistedPackets() {
        return this.whitelistedClientPackets;
    }

    public BiMap<Integer, Class<? extends Packet>> getServerSideWhitelistedPackets() {
        return this.whitelistedServerPackets;
    }

    public int getStateFlag() {
        return this.stateFlag;
    }

    private static final TIntObjectMap<ConnectionState>                connectionStates = new TIntObjectHashMap();
    private static final Map<Class<? extends Packet>, ConnectionState> packetMap        = Maps.newHashMap();

    private static boolean packetsRegistered = false;

    public static ConnectionState getConnectionStateFromStateFlag(int stateFlag) {
        return connectionStates.get(stateFlag);
    }

    public static ConnectionState getConnectionStateFromPacket(Packet packet) {
        return packetMap.get(packet.getClass());
    }

    public static void registerPackets() throws Exception {
        if (packetsRegistered) {
            return;
        }

        try {
            registerHandshakePackets();
            registerPlayPackets();
            registerLoginPackets();
        }

        catch (IllegalAddException ex) {
            return;
        }

        ConnectionState[] states = values();

        for (int i = 0; i < states.length; i++) {
            ConnectionState state = states[i];
            connectionStates.put(state.getStateFlag(), state);

            Iterator<Class<? extends Packet>> packetIterator = Iterables.concat(state.getClientSideWhitelistedPackets().values(), state.getServerSideWhitelistedPackets().values()).iterator();

            while (packetIterator.hasNext()) {
                Class<? extends Packet> clazz = packetIterator.next();

                if (packetMap.containsKey(clazz) && packetMap.get(clazz) != state) {
                    String error = "Packet " + clazz.getCanonicalName() + " is already assigned to " + packetMap.get(clazz) + " - which cannot be reassigned to: " + state;
                    JDPAD.getInstance().handleError(error, null, true);
                    throw new IllegalAddException(error);
                }
            }
        }

        packetsRegistered = true;
    }

    private static void registerHandshakePackets() throws IllegalAddException {
        ConnectionState.HANDSHAKING.addClientPacket(Packet.class);
        ConnectionState.HANDSHAKING.addServerPacket(Packet.class);
    }

    private static void registerPlayPackets() throws IllegalAddException {

    }

    private static void registerLoginPackets() throws IllegalAddException {

    }
}
