package jonathansmith.dpad.api.common.engine.event;

import java.util.List;

import jonathansmith.dpad.common.engine.event.Event;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Interface used to subscribe to engine event threads
 */
public interface IEventListener {

    List<Class<? extends Event>> getEventsToListenFor();

    void onEventRecieved(Event event);
}
