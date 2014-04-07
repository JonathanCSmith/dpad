package jonathansmith.dpad.common.network;

import java.net.SocketAddress;
import java.util.Queue;

import com.google.common.collect.BiMap;
import com.google.common.collect.Queues;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.network.listener.PacketListenersTuple;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Core session class for dealing with network states between the client and server
 */
public abstract class NetworkSession extends SimpleChannelInboundHandler {

    public static final AttributeKey<ConnectionState>                         CONNECTION_STATE_ATTRIBUTE_KEY               = new AttributeKey("Connection State");
    public static final AttributeKey<BiMap<Integer, Class<? extends Packet>>> WHITELISTED_RECEIVABLE_PACKETS_ATTRIBUTE_KEY = new AttributeKey("Receivable Packets");
    public static final AttributeKey<BiMap<Integer, Class<? extends Packet>>> WHITELISTED_SENDABLE_PACKETS_ATTRIBUTE_KEY   = new AttributeKey("Sendable Packets");

    private final Queue<PacketListenersTuple> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
    private final Queue<Packet>               inboundPacketsQueue  = Queues.newConcurrentLinkedQueue();

    protected final IEngine engine;

    private final boolean isClientSide;

    private NetworkProtocol networkProtocol;
    private Channel         channel;
    private SocketAddress   address;
    private ConnectionState connectionState;
    private String          terminationReason;

    public NetworkSession(IEngine engine, boolean isClientSide) {
        this.engine = engine;
        this.isClientSide = isClientSide;
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public NetworkProtocol getNetworkProtocol() {
        return this.networkProtocol;
    }

    public void setNetworkProtocol(NetworkProtocol networkProtocol) {
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
        this.channel.attr(WHITELISTED_RECEIVABLE_PACKETS_ATTRIBUTE_KEY).set(state.getReceivablePacketsForSide(this.isClientSide));
        this.channel.attr(WHITELISTED_SENDABLE_PACKETS_ATTRIBUTE_KEY).set(state.getSendablePacketsForSide(this.isClientSide));
        this.channel.config().setAutoRead(true);
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isLocalChannel() {
        return this.isClientSide;
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
            this.outboundPacketsQueue.add(new PacketListenersTuple(packet, listeners));
        }
    }

    private void flushOutboundQueue() {
        if (this.isChannelOpen()) {
            while (!this.outboundPacketsQueue.isEmpty()) {
                PacketListenersTuple listener = this.outboundPacketsQueue.poll();
                this.dispatchPacket(listener.getPacket(), listener.getListeners());
            }
        }
    }

    private void dispatchPacket(final Packet packet, final GenericFutureListener[] listeners) {
        final ConnectionState packetsRegisteredConnectionState = ConnectionState.getConnectionStateFromPacket(packet);
        final ConnectionState channelConnectionState = this.channel.attr(CONNECTION_STATE_ATTRIBUTE_KEY).get();

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
                p.processPacket(this.networkProtocol);
            }

            else {
                this.inboundPacketsQueue.add(p);
            }
        }
    }

    public void processReceivedPackets() {
        this.flushOutboundQueue();
        ConnectionState connectionState = this.channel.attr(CONNECTION_STATE_ATTRIBUTE_KEY).get();

        if (this.connectionState != connectionState) {
            if (this.connectionState != null) {
                this.networkProtocol.onConnectionStateTransition(this.connectionState, connectionState);
            }

            this.connectionState = connectionState;
        }

        if (this.networkProtocol != null) {
            for (int i = 1000; !this.inboundPacketsQueue.isEmpty() && i >= 0; i--) {
                Packet packet = this.inboundPacketsQueue.poll();
                packet.processPacket(this.networkProtocol);
            }

            this.networkProtocol.pulseRepeatPackets();
        }

        this.channel.flush();
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
        String info = "NETWORK: channel closed due to exception";
        this.engine.info(info, reason);
        this.closeChannel(info);
    }
}
