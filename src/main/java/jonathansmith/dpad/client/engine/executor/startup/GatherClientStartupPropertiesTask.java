package jonathansmith.dpad.client.engine.executor.startup;

import java.util.ArrayList;
import java.util.List;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.engine.event.ClientStartupPropertiesFinishEvent;
import jonathansmith.dpad.client.gui.startup.ClientStartupPropertiesDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Gathers the client startup properties. Only used if not a local instance (whereby data is stored together)
 */
public class GatherClientStartupPropertiesTask extends Task implements IEventListener {

    private static final String                            TASK_NAME = "Gather Client Startup Properties";
    private static final ArrayList<Class<? extends Event>> EVENTS    = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(ClientStartupPropertiesFinishEvent.class);
    }

    private boolean isWaiting = true;
    private boolean isKilled  = false;

    public GatherClientStartupPropertiesTask(IEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().addEventListener(this);
        ClientStartupPropertiesDisplay display = new ClientStartupPropertiesDisplay((ClientEngine) this.loggingEngine);
        ClientDisplayChangeEvent event = new ClientDisplayChangeEvent(display);
        this.loggingEngine.getEventThread().postEvent(event);

        while (this.isWaiting && this.isKilled) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {

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
        this.isWaiting = false;
    }
}
