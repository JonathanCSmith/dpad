package jonathansmith.dpad.common.network;

import java.net.SocketAddress;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.server.engine.util.config.AddressList;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Core network manager/thread for handling network events on the server
 */
public abstract class NetworkManager extends Thread {

    protected final IEngine engine;

    private final AddressList blackListedConnections = new AddressList();
    private final AddressList whiteListedConnections = new AddressList();

    private final SocketAddress     address;
    private final NioEventLoopGroup eventLoopGroup;
    private final boolean           isLocalConnection;
    private final boolean           isServerWhiteListed;

    protected boolean isAlive     = false;
    protected boolean hasErrored  = false;
    protected boolean hasShutdown = false;

    private ChannelFuture channelFuture;

    public NetworkManager(IEngine engine, SocketAddress address, String eventThreadNameFormat, boolean isLocal) {
        this.engine = engine;
        this.address = address;
        this.eventLoopGroup = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat(eventThreadNameFormat).setDaemon(true).build());
        this.isLocalConnection = isLocal;
        this.isServerWhiteListed = false; // TODO:
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

    public ChannelFuture getChannelFuture() {
        return this.channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

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

    public abstract void buildBootstap();

    @Override
    public void start() {
        this.isAlive = true;
        super.start();
    }

    @Override
    public abstract void run();

    public String allowUserToConnect(NetworkSession networkSession) {
        if (this.blackListedConnections.isPresent(networkSession.getAddress(), networkSession.getPort())) {
            return "Banned from the server!";
        }

        else if (this.isServerWhiteListed && !this.whiteListedConnections.isPresent(networkSession.getAddress(), networkSession.getPort())) {
            return "Not white listed for this server!";
        }

        else {
            return null;
        }
    }
}
