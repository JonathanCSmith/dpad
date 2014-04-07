package jonathansmith.dpad.common.network.channel;

import io.netty.channel.ChannelInitializer;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.network.NetworkManager;
import jonathansmith.dpad.common.network.NetworkSession;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Generic channel initialiser with all pertinent internal references
 */
public abstract class ChannelInitialiser extends ChannelInitializer {

    protected final IEngine        engine;
    protected final NetworkManager networkManager;

    private NetworkSession session;

    public ChannelInitialiser(IEngine engine, NetworkManager manager) {
        this.engine = engine;
        this.networkManager = manager;
    }
}
