package jonathansmith.dpad.common.network;

import java.net.SocketAddress;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;

import jonathansmith.dpad.common.engine.Engine;

import jonathansmith.dpad.server.engine.util.config.AddressList;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Core network manager/thread for handling network events on the server
 */
public abstract class NetworkManager extends Thread {

    protected final Engine engine;

    private final AddressList black_listed_connections = new AddressList();
    private final AddressList white_listed_connections = new AddressList();

    private final SocketAddress     address;
    private final NioEventLoopGroup event_loop_group;
    private final boolean           is_local_connection;
    private final boolean           is_server_white_listed;

    protected boolean isAlive     = false;
    protected boolean hasError    = false;
    protected boolean hasShutdown = false;

    private ChannelFuture channelFuture;

    public NetworkManager(Engine engine, SocketAddress address, String eventThreadNameFormat, boolean isLocal) {
        this.engine = engine;
        this.address = address;
        this.event_loop_group = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat(eventThreadNameFormat).setDaemon(true).build());
        this.is_local_connection = isLocal;
        this.is_server_white_listed = false;
    }

    public SocketAddress getSocketAddress() {
        return this.address;
    }

    public NioEventLoopGroup getEventLoopGroups() {
        return this.event_loop_group;
    }

    public boolean isLocalConnection() {
        return this.is_local_connection;
    }

    public ChannelFuture getChannelFuture() {
        return this.channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public void shutdown(boolean force) {
        this.isAlive = false;
        this.hasError |= force;

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

        // Notify engine thread that the network has been shutdown so it can handle accordingly
        if (!this.engine.isShuttingDown()) {
            if (force) {
                this.engine.handleError("Shutdown called on network thread with a fatal route cause.", null);
            }

            else {
                this.engine.handleShutdown("Shutdown called on network thread with a non detrimental route cause");
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
            this.event_loop_group.shutdownGracefully();
            this.engine.info("Shutdown network event threads", null);
        }
    }

    public abstract void buildBootstrap();

    @Override
    public void start() {
        this.isAlive = true;
        super.start();
    }

    @Override
    public abstract void run();

    public String allowUserToConnect(NetworkSession networkSession) {
        if (this.black_listed_connections.isPresent(networkSession.getAddress(), networkSession.getPort())) {
            return "Banned from the server!";
        }

        else if (this.is_server_white_listed && !this.white_listed_connections.isPresent(networkSession.getAddress(), networkSession.getPort())) {
            return "Not white listed for this server!";
        }

        else {
            return null;
        }
    }
}
