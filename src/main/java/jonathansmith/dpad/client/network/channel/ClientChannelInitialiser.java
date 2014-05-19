package jonathansmith.dpad.client.network.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.client.network.ClientNetworkSession;
import jonathansmith.dpad.common.network.NetworkManager;
import jonathansmith.dpad.common.network.channel.*;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Client side channel initialiser. Used to setup the client side of a network channel for proper data handling.
 */
public class ClientChannelInitialiser extends ChannelInitialiser {

    private static final int TIMEOUT_TIME = 20;

    public ClientChannelInitialiser(IEngine engine, NetworkManager manager) {
        super(engine, manager);
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        if (!this.networkManager.isLocalConnection()) {
            channel.config().setOption(ChannelOption.IP_TOS, 24);
            channel.config().setOption(ChannelOption.TCP_NODELAY, false);
            channel.pipeline().addLast("timeout_handler", new ReadTimeoutHandler(TIMEOUT_TIME));
            channel.pipeline().addLast("split_handler", new MessageSplitter());
            channel.pipeline().addLast("decode_handler", new MessageDecoder(this.engine));
            channel.pipeline().addLast("prepend_handler", new MessagePrepender());
            channel.pipeline().addLast("encode_handler", new MessageEncoder(this.engine));
        }

        ClientNetworkSession session = new ClientNetworkSession(this.engine, this.networkManager.isLocalConnection());
        channel.pipeline().addLast("packet_handler", session);
        this.networkManager.addSession(session);
    }
}
