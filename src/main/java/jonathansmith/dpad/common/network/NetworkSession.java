package jonathansmith.dpad.common.network;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.UUID;

import javax.crypto.SecretKey;

import com.google.common.collect.BiMap;
import com.google.common.collect.Queues;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.crypto.CryptographyManager;
import jonathansmith.dpad.common.network.channel.EncryptionDecoder;
import jonathansmith.dpad.common.network.channel.EncryptionEncoder;
import jonathansmith.dpad.common.network.listener.PacketListenersTuple;
import jonathansmith.dpad.common.network.packet.KeepAlivePacket;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Core session class for dealing with network states between the client and server
 */
public abstract class NetworkSession extends SimpleChannelInboundHandler {

    public static final AttributeKey<ConnectionState>                         CONNECTION_STATE_ATTRIBUTE_KEY                = new AttributeKey<ConnectionState>("Connection State");
    public static final AttributeKey<BiMap<Integer, Class<? extends Packet>>> WHITE_LISTED_RECEIVABLE_PACKETS_ATTRIBUTE_KEY = new AttributeKey<BiMap<Integer, Class<? extends Packet>>>("Receivable Packets");
    public static final AttributeKey<BiMap<Integer, Class<? extends Packet>>> WHITE_LISTED_SENDABLE_PACKETS_ATTRIBUTE_KEY   = new AttributeKey<BiMap<Integer, Class<? extends Packet>>>("Sendable Packets");

    protected final IEngine engine;

    private final Queue<PacketListenersTuple> outbound_packets_queue = Queues.newConcurrentLinkedQueue();
    private final Queue<Packet>               inbound_packets_queue  = Queues.newConcurrentLinkedQueue();

    private final UUID    local_UUID;
    private final boolean is_local_connection;
    private final boolean is_client_side;

    private Channel channel              = null;
    private long    timeSinceLastProcess = 0L;

    private INetworkProtocol networkProtocol;
    private SocketAddress    address;
    private ConnectionState  connectionState;
    private String           terminationReason;
    private UUID             foreignUUID;

    public NetworkSession(IEngine engine, SocketAddress address, boolean isLocal, boolean isClient) {
        this.engine = engine;
        this.address = address;
        this.local_UUID = UUID.randomUUID();
        this.is_local_connection = isLocal;
        this.is_client_side = isClient;
    }

    public SocketAddress getSocketAddress() {
        return this.address;
    }

    public String getAddress() {
        String[] bits = this.address.toString().split(":");
        return bits[0];
    }

    public String getPort() {
        return this.address.toString().split(":")[1];
    }

    public String getEngineAssignedUUID() {
        return this.local_UUID.toString();
    }

    public void assignForeignUUID(String foreignUUID) {
        this.foreignUUID = UUID.fromString(foreignUUID);
    }

    public String getForeignUUID() {
        return this.foreignUUID == null ? "NULL" : this.foreignUUID.toString();
    }

    public INetworkProtocol getNetworkProtocol() {
        return this.networkProtocol;
    }

    public void setNetworkProtocol(INetworkProtocol networkProtocol) {
        if (networkProtocol == null) {
            return;
        }

        String oldName = "NULL";
        if (this.networkProtocol != null) {
            oldName = this.networkProtocol.getProtocolName();
        }

        this.networkProtocol = networkProtocol;
        this.engine.info("Switched network protocol handler from: " + oldName + " to: " + this.networkProtocol.getProtocolName(), null);
    }

    public void setConnectionState(ConnectionState state) {
        this.connectionState = this.channel.attr(CONNECTION_STATE_ATTRIBUTE_KEY).getAndSet(state);
        this.channel.attr(WHITE_LISTED_RECEIVABLE_PACKETS_ATTRIBUTE_KEY).set(state.getReceivablePacketsForSide(this.is_client_side));
        this.channel.attr(WHITE_LISTED_SENDABLE_PACKETS_ATTRIBUTE_KEY).set(state.getSendablePacketsForSide(this.is_client_side));
        this.channel.config().setAutoRead(true);
    }

    public boolean hasChannelInitialised() {
        return this.channel != null;
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isLocalChannel() {
        return this.is_local_connection;
    }

    public boolean isClientChannel() {
        return this.is_client_side;
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    public void scheduleOutboundPacket(Packet packet, GenericFutureListener[] listeners) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packet, listeners);
        }

        else {
            this.outbound_packets_queue.add(new PacketListenersTuple(packet, listeners));
        }
    }

    private void flushOutboundQueue() {
        if (this.isChannelOpen()) {
            while (!this.outbound_packets_queue.isEmpty()) {
                PacketListenersTuple listener = this.outbound_packets_queue.poll();
                this.dispatchPacket(listener.getPacket(), listener.getListeners());
            }
        }
    }

    private void dispatchPacket(final Packet packet, final GenericFutureListener[] listeners) {
        if (packet.getClass() != KeepAlivePacket.class) {
            this.engine.debug("Dispatching packet: " + packet.getClass(), null);
        }

        final ConnectionState packetsRegisteredConnectionState = ConnectionState.getConnectionStateFromPacket(packet);
        final ConnectionState channelConnectionState = this.channel.attr(CONNECTION_STATE_ATTRIBUTE_KEY).get();

        if (packetsRegisteredConnectionState == null) {
            this.engine.error("Attempted to dispatch an unregistered packet", null);
            this.shutdown(true);
            return;
        }

        if (packetsRegisteredConnectionState != channelConnectionState) {
            this.engine.info("Disabling autoread of packet due to channel state mismatch", null);
            this.disableAutoRead();
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (packetsRegisteredConnectionState != channelConnectionState) {
                this.setConnectionState(packetsRegisteredConnectionState);
            }

            this.channel.writeAndFlush(packet).addListeners(listeners).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }

        else {
            this.channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    if (packetsRegisteredConnectionState != channelConnectionState) {
                        NetworkSession.this.setConnectionState(packetsRegisteredConnectionState);
                    }

                    NetworkSession.this.channel.writeAndFlush(packet).addListeners(listeners).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    private void readPacket(ChannelHandlerContext ctx, Packet p) {
        if (this.isChannelOpen()) {
            if (p.isUrgent()) {
                this.engine.debug("Processing packet: " + p.getClass(), null);
                p.processPacket(this.networkProtocol);
            }

            else {
                this.inbound_packets_queue.add(p);
            }
        }
    }

    public void processReceivedPackets() {
        if (this.isProcessLocked()) {
            return;
        }

        this.flushOutboundQueue();
        ConnectionState connectionState = this.channel.attr(CONNECTION_STATE_ATTRIBUTE_KEY).get();

        if (this.connectionState != connectionState) {
            if (this.connectionState != null) {
                this.networkProtocol.onConnectionStateTransition(this.connectionState, connectionState);
            }

            this.connectionState = connectionState;
        }

        if (this.networkProtocol != null) {
            for (int i = 1000; !this.inbound_packets_queue.isEmpty() && i >= 0; i--) {
                Packet packet = this.inbound_packets_queue.poll();

                if (packet.getClass() != KeepAlivePacket.class) {
                    this.engine.debug("Processing packet: " + packet.getClass(), null);
                }

                packet.processPacket(this.networkProtocol);
            }

            this.networkProtocol.pulseScheduledProtocolTasks();
        }

        this.channel.flush();
    }

    private boolean isProcessLocked() {
        if (this.timeSinceLastProcess == 0L) {
            this.timeSinceLastProcess = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - this.timeSinceLastProcess < 50) {
            return true;
        }

        else {
            this.timeSinceLastProcess = System.currentTimeMillis();
            return false;
        }
    }

    public void enableEncryption(SecretKey secretKey) {
        this.engine.trace("Switching to encrypted channels", null);
        this.channel.pipeline().addBefore("split_handler", "decryption_handler", new EncryptionDecoder(CryptographyManager.getCipher(2, secretKey)));
        this.channel.pipeline().addBefore("prepend_handler", "encryption_handler", new EncryptionEncoder(CryptographyManager.getCipher(1, secretKey)));
    }

    public String buildSessionInformation() {
        return "Address: " + this.getAddress() + ", Port: " + this.getPort() + ", Local UUID: " + this.getEngineAssignedUUID() + ", Foreign UUID: " + this.getForeignUUID();
    }

    public String getExitMessage() {
        return this.terminationReason;
    }

    public void closeChannel(String reason) {
        if (this.channel.isOpen()) {
            this.channel.close();
            this.terminationReason = reason;
        }
    }

    public void shutdown(boolean force) {
        this.closeChannel("Shutdown request received, request has status: " + force);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object o) {
        this.readPacket(ctx, (Packet) o);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        this.channel = ctx.channel();
        this.address = this.channel.remoteAddress();
        this.setConnectionState(ConnectionState.LOGIN);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String info = "NETWORK: channel closed due to the channel becoming inactive (timeout or closure).";
        this.engine.info(info, null);
        this.closeChannel(info);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable reason) {
        this.closeChannel("NETWORK: channel closed due to exception: " + reason.toString());
    }
}
