package jonathansmith.dpad.common.network.channel;

import io.netty.channel.ChannelInitializer;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Generic channel initialiser with all pertinent internal references
 */
public abstract class ChannelInitialiser extends ChannelInitializer {

    protected final Engine engine;

    public ChannelInitialiser(Engine engine) {
        this.engine = engine;
    }
}
