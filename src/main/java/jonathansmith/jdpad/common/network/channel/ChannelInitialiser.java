package jonathansmith.jdpad.common.network.channel;

import io.netty.channel.ChannelInitializer;

import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.network.NetworkManager;
import jonathansmith.jdpad.common.network.NetworkSession;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Generic channel initialiser with all pertinent internal references
 */
public abstract class ChannelInitialiser extends ChannelInitializer {

    protected final Engine         engine;
    protected final NetworkManager networkManager;

    private NetworkSession session;

    public ChannelInitialiser(Engine engine, NetworkManager manager) {
        this.engine = engine;
        this.networkManager = manager;
    }
}
