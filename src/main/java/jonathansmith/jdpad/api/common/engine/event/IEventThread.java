package jonathansmith.jdpad.api.common.engine.event;

import jonathansmith.jdpad.common.engine.event.Event;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Methods for subscribing to and posting events
 */
public interface IEventThread {
    void shutdown(boolean force);

    void addEventListener(IEventListener listener) throws InterruptedException;

    void removeListener(IEventListener listener) throws InterruptedException;

    void postEvent(Event event) throws InterruptedException;
}
