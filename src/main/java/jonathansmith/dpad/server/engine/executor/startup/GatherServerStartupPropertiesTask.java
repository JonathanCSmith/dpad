package jonathansmith.dpad.server.engine.executor.startup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.engine.event.ServerDisplayChangeEvent;
import jonathansmith.dpad.server.engine.event.ServerStartupPropertiesFinishEvent;
import jonathansmith.dpad.server.engine.util.config.ServerStartupProperties;
import jonathansmith.dpad.server.gui.startup.ServerStartupPropertiesDisplay;

/**
 * Created by Jon on 13/07/2014.
 * <p/>
 * Server startup properties class
 */
public class GatherServerStartupPropertiesTask extends Task implements IEventListener {

    private static final String                            TASK_NAME = "Gather Server Startup Properties";
    private static final ArrayList<Class<? extends Event>> EVENTS    = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(ServerStartupPropertiesFinishEvent.class);
    }

    private boolean isWaiting = true;
    private boolean isKilled  = false;

    private ServerStartupProperties properties;

    public GatherServerStartupPropertiesTask(ServerEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    public void runTask() {
        this.loggingEngine.getEventThread().addEventListener(this);
        ServerStartupPropertiesDisplay display = new ServerStartupPropertiesDisplay((ServerEngine) this.loggingEngine);
        ServerDisplayChangeEvent event = new ServerDisplayChangeEvent(display);
        this.loggingEngine.getEventThread().postEvent(event);

        while (this.isWaiting && !this.isKilled) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                // No matter - we don't need to pass this up
            }
        }

        this.loggingEngine.getEventThread().removeListener(this);
    }

    @Override
    protected void killTask() {
        this.isKilled = true;
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        this.properties = ((ServerStartupPropertiesFinishEvent) event).getProperties();
        ((ServerEngine) this.loggingEngine).setServerUUID(UUID.nameUUIDFromBytes(this.properties.getSuperUsername().getBytes()));
        this.isWaiting = false;
    }

    public ServerStartupProperties getServerSetupConfiguration() {
        return this.properties;
    }
}
