package jonathansmith.dpad.server.engine.event;

import jonathansmith.dpad.common.engine.event.Event;

import jonathansmith.dpad.server.engine.util.config.ServerStartupProperties;

/**
 * Created by Jon on 13/07/2014.
 * <p/>
 * Event for the setup of server configuration.
 */
public class ServerStartupPropertiesFinishEvent extends Event {

    private final ServerStartupProperties config;

    public ServerStartupPropertiesFinishEvent(ServerStartupProperties configuration) {
        this.config = configuration;
    }

    public ServerStartupProperties getProperties() {
        return this.config;
    }
}
