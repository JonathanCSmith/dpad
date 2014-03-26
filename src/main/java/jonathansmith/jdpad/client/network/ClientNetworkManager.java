package jonathansmith.jdpad.client.network;

import java.net.SocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.network.NetworkManager;

import jonathansmith.jdpad.client.network.channel.ClientChannelInitialiser;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network manager
 */
public class ClientNetworkManager extends NetworkManager {

    private final Bootstrap clientBootstrap;

    public ClientNetworkManager(Engine engine, SocketAddress address, boolean isLocal) {
        super(engine, address, "Netty Client IO #%d", isLocal);

        this.clientBootstrap = new Bootstrap();
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
}
