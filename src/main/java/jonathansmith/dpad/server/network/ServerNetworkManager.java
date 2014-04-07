package jonathansmith.dpad.server.network;

import java.net.SocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.network.NetworkManager;
import jonathansmith.dpad.server.network.channel.ServerChannelInitialiser;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Server side network manager
 */
public class ServerNetworkManager extends NetworkManager {

    private final ServerBootstrap serverBootstrap;

    public ServerNetworkManager(Engine engine, SocketAddress address, boolean isLocal) {
        super(engine, address, "Netty Server IO #%d", isLocal);

        this.serverBootstrap = new ServerBootstrap();
    }

    @Override
    public void buildBootstap() {
        this.serverBootstrap.group(this.getEventLoopGroups());
        this.serverBootstrap.childHandler(new ServerChannelInitialiser(this.engine, this));

        if (this.isLocalConnection()) {
            this.serverBootstrap.channel(LocalServerChannel.class);
        }

        else {
            this.serverBootstrap.channel(NioServerSocketChannel.class);
        }

        this.setChannelFuture(this.serverBootstrap.localAddress(this.getSocketAddress()).bind());
        this.getChannelFuture().syncUninterruptibly();
    }
}
