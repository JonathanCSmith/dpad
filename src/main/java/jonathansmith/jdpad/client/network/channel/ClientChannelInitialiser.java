package jonathansmith.jdpad.client.network.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.network.NetworkManager;
import jonathansmith.jdpad.common.network.channel.*;

import jonathansmith.jdpad.client.network.ClientNetworkSession;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Client side channel initialiser
 */
public class ClientChannelInitialiser extends ChannelInitialiser {

    private static final int TIMEOUT_TIME = 20;

    public ClientChannelInitialiser(Engine engine, NetworkManager manager) {
        super(engine, manager);
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        if (!this.networkManager.isLocalConnection()) {
            channel.config().setOption(ChannelOption.IP_TOS, 24);
            channel.config().setOption(ChannelOption.TCP_NODELAY, false);
            channel.pipeline().addLast("timeout_handler", new ReadTimeoutHandler(TIMEOUT_TIME));
            channel.pipeline().addLast("split_handler", new MessageSplitter());
            channel.pipeline().addLast("decode_handler", new MessageDecoder(this.engine.getLogger()));
            channel.pipeline().addLast("prepend_handler", new MessagePrepender());
            channel.pipeline().addLast("encode_handler", new MessageEncoder());
        }

        ClientNetworkSession session = new ClientNetworkSession(this.engine, this.networkManager.isLocalConnection());
        channel.pipeline().addLast("packet_handler", session);
        this.networkManager.addSession(session);
    }
}
