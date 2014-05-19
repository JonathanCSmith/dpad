package jonathansmith.dpad.client.network;

import java.net.SocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import jonathansmith.dpad.client.network.channel.ClientChannelInitialiser;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.network.NetworkManager;
import jonathansmith.dpad.common.network.NetworkSession;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network manager. Builds the client side bootstrap for connecting to a provided server.
 * If the user has specified a local connection a dummy network channel is created to improve memory usage.
 */
public class ClientNetworkManager extends NetworkManager {

    private final Bootstrap clientBootstrap;

    private NetworkSession session = null;

    public ClientNetworkManager(Engine engine, SocketAddress address, boolean isLocal) {
        super(engine, address, "Netty Client IO #%d", isLocal);

        this.clientBootstrap = new Bootstrap();
    }

    @Override
    public void addSession(NetworkSession session) {
        if (this.session != null) {
            this.engine.handleError("Cannot override session", new IllegalStateException("Session data has already been established"), true);
        }
    }

    @Override
    public void buildBootstap() {
        this.clientBootstrap.group(this.getEventLoopGroups());
        this.clientBootstrap.handler(new ClientChannelInitialiser(this.engine, this));

        if (this.isLocalConnection()) {
            this.clientBootstrap.channel(LocalChannel.class);
        }

        else {
            this.clientBootstrap.channel(NioSocketChannel.class);
        }

        this.setChannelFuture(this.clientBootstrap.connect(this.getSocketAddress()));
        this.getChannelFuture().syncUninterruptibly();
    }

    @Override
    public void run() {
        while (this.isAlive && !this.hasErrored) {
            if (this.session.isChannelOpen()) {
                this.session.processReceivedPackets();
            }

            else if (this.session.getExitMessage() != null) {
                this.session.getNetworkProtocol().onDisconnect(this.session.getExitMessage());
            }

            else {
                this.session.getNetworkProtocol().onDisconnect("Disconnected from Server");
            }
        }
    }
}
