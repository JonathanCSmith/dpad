package jonathansmith.dpad.common.network;

import java.util.Map;

import com.beust.jcommander.internal.Maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

import jonathansmith.dpad.common.network.packet.DisconnectPacket;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.login.EncryptionRequestPacket;
import jonathansmith.dpad.common.network.packet.login.EncryptionResponsePacket;
import jonathansmith.dpad.common.network.packet.login.LoginStartPacket;
import jonathansmith.dpad.common.network.packet.login.LoginSuccessPacket;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import jonathansmith.dpad.DPAD;
import org.dom4j.IllegalAddException;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Enum representing all possible connection states between the client and server. Packets are white listed to prevent unauthorised communications.
 */
public enum ConnectionState {

    LOGIN(0),
    RUNTIME(1);
    private static final TIntObjectMap<ConnectionState>                connectionStates  = new TIntObjectHashMap<ConnectionState>();
    private static final Map<Class<? extends Packet>, ConnectionState> packetMap         = Maps.newHashMap();
    private static       boolean                                       packetsRegistered = false;
    private final int                                     stateFlag;
    private final BiMap<Integer, Class<? extends Packet>> whiteListedClientPackets;
    private final BiMap<Integer, Class<? extends Packet>> whiteListedServerPackets;

    private ConnectionState(int stateFlag) {
        this.stateFlag = stateFlag;
        this.whiteListedClientPackets = HashBiMap.create();
        this.whiteListedServerPackets = HashBiMap.create();
    }

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
            registerLoginPackets();
            registerPlayPackets();
        }

        catch (IllegalAddException ex) {
            return;
        }

        ConnectionState[] states = values();

        for (ConnectionState state : states) {
            connectionStates.put(state.getStateFlag(), state);

            for (Class<? extends Packet> clazz : Iterables.concat(state.getClientSidePackets().values(), state.getServerSidePackets().values())) {
                if (packetMap.containsKey(clazz) && packetMap.get(clazz) != state) {
                    String error = "Packet " + clazz.getCanonicalName() + " is already assigned to " + packetMap.get(clazz) + " - which cannot be reassigned to: " + state;
                    DPAD.getInstance().handleError(error, null, true);
                    throw new IllegalAddException(error);
                }
            }
        }

        packetsRegistered = true;
    }

    private static void registerLoginPackets() throws IllegalAddException {
        ConnectionState.LOGIN.addClientPacket(LoginStartPacket.class);
        ConnectionState.LOGIN.addServerPacket(EncryptionRequestPacket.class);
        ConnectionState.LOGIN.addClientPacket(EncryptionResponsePacket.class);
        ConnectionState.LOGIN.addServerPacket(LoginSuccessPacket.class);
        ConnectionState.LOGIN.addServerPacket(DisconnectPacket.class);
    }

    private static void registerPlayPackets() throws IllegalAddException {
        ConnectionState.RUNTIME.addServerPacket(DisconnectPacket.class);
    }

    // Add packet to allowed sendables from client
    protected void addClientPacket(Class<? extends Packet> clazz) throws IllegalAddException {
        if (this.addPacket(clazz, this.whiteListedClientPackets)) {
            String error = "Failure to register client packet: " + clazz.getCanonicalName();
            DPAD.getInstance().handleError(error, null, true);
            throw new IllegalAddException(error);
        }
    }

    // Add packet to allowed sendables from server
    protected void addServerPacket(Class<? extends Packet> clazz) throws IllegalAddException {
        if (!this.addPacket(clazz, this.whiteListedServerPackets)) {
            String error = "Failure to register client packet: " + clazz.getCanonicalName();
            DPAD.getInstance().handleError(error, null, true);
            throw new IllegalAddException(error);
        }
    }

    // Add packet to allowed sendables
    private boolean addPacket(Class<? extends Packet> clazz, BiMap<Integer, Class<? extends Packet>> map) {
        if (map.containsValue(clazz)) {
            return false;
        }

        int id = map.size();
        map.put(id, clazz);
        return true;
    }

    public BiMap<Integer, Class<? extends Packet>> getReceivablePacketsForSide(boolean isClientSide) {
        return isClientSide ? this.getServerSidePackets() : this.getClientSidePackets();
    }

    public BiMap<Integer, Class<? extends Packet>> getSendablePacketsForSide(boolean isClientSide) {
        return isClientSide ? this.getClientSidePackets() : this.getServerSidePackets();
    }

    public BiMap<Integer, Class<? extends Packet>> getClientSidePackets() {
        return this.whiteListedClientPackets;
    }

    public BiMap<Integer, Class<? extends Packet>> getServerSidePackets() {
        return this.whiteListedServerPackets;
    }

    public int getStateFlag() {
        return this.stateFlag;
    }
}
