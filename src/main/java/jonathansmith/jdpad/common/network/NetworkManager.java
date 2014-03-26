package jonathansmith.jdpad.common.network;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.network.packet.DisconnectPacket;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Core network manager/thread for handling network events
 */
public abstract class NetworkManager extends Thread {

    protected final List<NetworkSession> sessions = Collections.synchronizedList(new ArrayList<NetworkSession>());

    protected final Engine engine;

    private final SocketAddress     address;
    private final NioEventLoopGroup eventLoopGroup;
    private final boolean           isLocalConnection;

    private boolean isAlive     = false;
    private boolean hasErrored  = false;
    private boolean hasShutdown = false;

    private ChannelFuture channelFuture;

    public NetworkManager(Engine engine, SocketAddress address, String eventThreadNameFormat, boolean isLocal) {
        this.engine = engine;
        this.address = address;
        this.eventLoopGroup = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat(eventThreadNameFormat).setDaemon(true).build());
        this.isLocalConnection = isLocal;
    }

    public SocketAddress getSocketAddress() {
        return this.address;
    }

    public NioEventLoopGroup getEventLoopGroups() {
        return this.eventLoopGroup;
    }

    public boolean isLocalConnection() {
        return this.isLocalConnection;
    }

    public void addSession(NetworkSession session) {
        this.sessions.add(session);
    }

    public ChannelFuture getChannelFuture() {
        return this.channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public abstract void buildBootstap();

    public void shutdown(boolean force) {
        this.isAlive = false;
        this.hasErrored |= force;

        while (!this.hasShutdown) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                this.engine.warn("Network thread was interrupted while shutting down", ex);
                force = true;
                break;
            }
        }

        try {
            this.channelFuture.channel().close().sync();
            this.engine.info("Shutdown network channel", null);
        }

        catch (InterruptedException ex) {
            this.engine.error("Could not complete shutdown of the network channel", ex);
        }

        finally {
            this.eventLoopGroup.shutdownGracefully();
            this.engine.info("Shutdown network event threads", null);
        }
    }

    @Override
    public void start() {
        this.isAlive = true;
    }

    @Override
    public void run() {
        while (this.isAlive && !this.hasErrored) {
            synchronized (this.sessions) {
                Iterator<NetworkSession> iter = this.sessions.iterator();

                while (iter.hasNext()) {
                    final NetworkSession session = iter.next();

                    if (!session.isChannelOpen()) {
                        iter.remove();

                        if (session.getExitMessage() != null) {
                            session.getNetworkProtocol().onDisconnect(session.getExitMessage());
                        }

                        else if (session.getNetworkProtocol() != null) {
                            session.getNetworkProtocol().onDisconnect("Disconnected");
                        }
                    }

                    else {
                        try {
                            session.processReceivedPackets();
                        }

                        catch (Exception ex) {
                            if (session.isLocalChannel()) {
                                this.engine.handleError("Error processing packets on local channel", ex, true);
                                this.isAlive = false;
                            }

                            else {
                                this.engine.warn("Failed to process inbound packet from: " + session.getAddress(), ex);
                            }

                            session.scheduleOutboundPacket(new DisconnectPacket("Internal server error"), new GenericFutureListener[]{new GenericFutureListener() {
                                @Override
                                public void operationComplete(Future f) {
                                    session.closeChannel("Internal Server Error");
                                }
                            }});

                            session.disableAutoRead();
                        }
                    }
                }
            }
        }

        for (final NetworkSession session : this.sessions) {
            if (session.isLocalChannel() && session.isChannelOpen()) {
                session.scheduleOutboundPacket(new DisconnectPacket("Server shutdown"), new GenericFutureListener[]{new GenericFutureListener() {
                    @Override
                    public void operationComplete(Future future) throws Exception {
                        session.shutdown(NetworkManager.this.hasErrored);
                    }
                }});
            }
        }

        this.hasShutdown = true;
    }
}
