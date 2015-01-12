package jonathansmith.dpad.api.plugins.events;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Methods for subscribing to and posting events
 */
public interface IEventThread {

    /**
     * Method to add an event listener into the event listener pool
     *
     * @param listener to add
     */
    void addEventListener(IEventListener listener);

    /**
     * Method to remove an event listener from the event listener pool
     *
     * @param listener to remove
     */
    void removeListener(IEventListener listener);

    /**
     * Post an event into the event thread for other listeners to respond to
     *
     * @param event
     */
    void postEvent(Event event);
}
