package jonathansmith.dpad.common.network;

import java.util.Map;

import com.beust.jcommander.internal.Maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

import jonathansmith.dpad.common.network.packet.DisconnectPacket;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.handshake.*;
import jonathansmith.dpad.common.network.packet.play.KeepAlivePacket;
import jonathansmith.dpad.common.network.packet.play.user.UserAdministrationResponsePacket;
import jonathansmith.dpad.common.network.packet.play.user.UserChangePasswordPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserLoginPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserLogoutPacket;

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

    HANDSHAKE(0),
    LOGIN(1),
    RUNTIME(2);
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

        registerHandshakePackets();
        registerLoginPackets();
        registerRuntimePackets();

        ConnectionState[] states = values();

        for (ConnectionState state : states) {
            connectionStates.put(state.getStateFlag(), state);

            for (Class<? extends Packet> clazz : Iterables.concat(state.getClientSidePackets().values(), state.getServerSidePackets().values())) {
                if (packetMap.containsKey(clazz) && packetMap.get(clazz) != state) {
                    String error = "Packet " + clazz.getCanonicalName() + " is already assigned to " + packetMap.get(clazz) + " - which cannot be reassigned to: " + state;
                    DPAD.getInstance().handleError(error, null, true);
                    throw new IllegalAddException(error);
                }

                packetMap.put(clazz, state);
            }
        }

        packetsRegistered = true;
    }

    private static void registerHandshakePackets() throws IllegalAddException {
        // Alls
        ConnectionState.HANDSHAKE.addServerPacket(DisconnectPacket.class);

        // Handshaking
        ConnectionState.HANDSHAKE.addClientPacket(HandshakeStartPacket.class);
        ConnectionState.HANDSHAKE.addServerPacket(EncryptionRequestPacket.class);
        ConnectionState.HANDSHAKE.addClientPacket(EncryptionResponsePacket.class);
        ConnectionState.HANDSHAKE.addServerPacket(HandshakeSuccessPacket.class);
        ConnectionState.HANDSHAKE.addClientPacket(HandshakeConfirmPacket.class);
    }

    private static void registerLoginPackets() throws IllegalAddException {
        // Alls
        ConnectionState.LOGIN.addClientPacket(DisconnectPacket.class);

        // User Administration Packets
        // TODO: Move encryption phase to user login
    }

    private static void registerRuntimePackets() throws IllegalAddException {
        // Alls
        ConnectionState.RUNTIME.addServerPacket(DisconnectPacket.class);

        // Server Admins
        ConnectionState.RUNTIME.addServerPacket(KeepAlivePacket.class);
        ConnectionState.RUNTIME.addClientPacket(KeepAlivePacket.class);

        // User
        ConnectionState.RUNTIME.addClientPacket(UserLoginPacket.class);
        ConnectionState.RUNTIME.addServerPacket(UserAdministrationResponsePacket.class);
        ConnectionState.RUNTIME.addClientPacket(UserChangePasswordPacket.class);
        ConnectionState.RUNTIME.addClientPacket(UserLogoutPacket.class);
        ConnectionState.RUNTIME.addServerPacket(UserAdministrationResponsePacket.class);
    }

    // Add packet to allowed send-ables from client
    protected void addClientPacket(Class<? extends Packet> clazz) throws IllegalAddException {
        if (!this.addPacket(clazz, this.whiteListedClientPackets)) {
            String error = "Failure to register client packet: " + clazz.getCanonicalName();
            DPAD.getInstance().handleError(error, null, true);
            throw new IllegalAddException(error);
        }
    }

    // Add packet to allowed send-ables from server
    protected void addServerPacket(Class<? extends Packet> clazz) throws IllegalAddException {
        if (!this.addPacket(clazz, this.whiteListedServerPackets)) {
            String error = "Failure to register client packet: " + clazz.getCanonicalName();
            DPAD.getInstance().handleError(error, null, true);
            throw new IllegalAddException(error);
        }
    }

    // Add packet to allowed send-ables
    private boolean addPacket(Class<? extends Packet> clazz, BiMap<Integer, Class<? extends Packet>> map) {
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
