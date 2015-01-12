package jonathansmith.dpad.api.plugins.events;

import java.util.List;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Interface used to subscribe to engine event threads
 */
public interface IEventListener {

    /**
     * Used to obtain events that this listener is interesting
     *
     * @return List of Classes (extended from event) that are of interest to this listener
     */
    List<Class<? extends Event>> getEventsToListenFor();

    /**
     * Called when an event occurs. Proper type checking should be performed within the listener as the event is only guaranteed to be of one of the types returned from {@link IEventListener#getEventsToListenFor()}
     *
     * @param event the event to which this listener is subscribed
     */
    void onEventReceived(Event event);
}
