package jonathansmith.dpad.common.engine.event;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import jonathansmith.dpad.api.common.engine.event.IEventListener;
import jonathansmith.dpad.api.common.engine.event.IEventThread;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Generic event thread implementation. Represents the core event processing behind an IEngine instance
 */
public class EventThread extends Thread implements IEventThread {

    private final Engine engine;

    private final Multimap<Class<? extends Event>, IEventListener> liveListenerMap             = LinkedListMultimap.create();
    private final Multimap<Class<? extends Event>, IEventListener> pendingListenerAdditionsMap = LinkedListMultimap.create();
    private final Multimap<Class<? extends Event>, IEventListener> pendingListenerRemovalMap   = LinkedListMultimap.create();
    private final List<Event>                                      pendingEventList            = new LinkedList<Event>();
    private final List<Event>                                      liveEventList               = new LinkedList<Event>();

    private boolean isAlive                               = false;
    private boolean isShuttingDown                        = false;
    private boolean pendingEventUpdates                   = false;
    private boolean isModifyingEventsList                 = false;
    private boolean pendingListenerAdditionsUpdate        = false;
    private boolean isModifyingEventListenersAdditionsMap = false;
    private boolean pendingListenerRemovalsUpdate         = false;
    private boolean isModifyingEventListenerRemovalMap    = false;

    public EventThread(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void start() {
        this.isAlive = true;
        super.start();
    }

    @Override
    public void run() {
        while (this.isAlive) {
            this.updateEventThread();

            if (this.liveEventList.isEmpty()) {
                try {
                    Thread.sleep(100);
                }

                catch (InterruptedException ex) {
                    this.engine.error("Event thread interrupted. Critical failure", ex);
                    this.shutdown(true);
                    continue;
                }
            }

            else {
                Event event = this.liveEventList.remove(0);
                Class<? extends Event> eventClass = event.getClass();
                Collection<IEventListener> listeners = this.liveListenerMap.get(eventClass);
                for (IEventListener listener : listeners) {
                    listener.onEventReceived(event);
                }
            }

            // If we are shutting down and all pending events have been posted kill the even thread
            if (this.liveEventList.isEmpty() && this.isShuttingDown) {
                this.isAlive = false;
            }
        }

        this.pendingListenerRemovalMap.clear();
        this.pendingListenerAdditionsMap.clear();
        this.liveListenerMap.clear();
        this.pendingEventList.clear();
        this.liveEventList.clear();
    }

    private void updateEventThread() {
        if (this.pendingListenerAdditionsUpdate && !this.isModifyingEventListenersAdditionsMap) {
            this.isModifyingEventListenersAdditionsMap = true;
            this.liveListenerMap.putAll(this.pendingListenerAdditionsMap);
            this.pendingListenerAdditionsMap.clear();
            this.isModifyingEventListenersAdditionsMap = false;
            this.pendingListenerAdditionsUpdate = false;
        }

        if (this.pendingListenerRemovalsUpdate && !this.isModifyingEventListenerRemovalMap) {
            this.isModifyingEventListenerRemovalMap = true;
            for (Map.Entry<Class<? extends Event>, IEventListener> entry : this.pendingListenerRemovalMap.entries()) {
                this.liveListenerMap.remove(entry.getKey(), entry.getValue());
            }
            this.pendingListenerRemovalMap.clear();
            this.isModifyingEventListenerRemovalMap = false;
            this.pendingListenerRemovalsUpdate = false;
        }

        if (this.pendingEventUpdates && !this.isModifyingEventsList) {
            this.isModifyingEventsList = true;
            this.liveEventList.addAll(this.pendingEventList);
            this.pendingEventList.clear();
            this.isModifyingEventsList = false;
            this.pendingEventUpdates = false;
        }
    }

    public void shutdown(boolean force) {
        if (!this.engine.isShuttingDown()) {
            if (force) {
                this.engine.handleError("Event thread shutdown caused by a fatal incident", null);
            }

            else {
                this.engine.handleShutdown("Event thread shutdown with a non-fatal route cause");
            }
        }

        if (force) {
            this.isAlive = false;
        }

        else {
            this.isShuttingDown = true;
        }
    }

    @Override
    public void addEventListener(IEventListener listener) {
        // Cannot add listeners when the event thread is shutting down
        if (this.isShuttingDown) {
            return;
        }

        List<Class<? extends Event>> events = listener.getEventsToListenFor();

        while (this.isModifyingEventListenersAdditionsMap) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                this.engine.error("Pending listener registration in event thread was interrupted", null);
                this.shutdown(true); // Cannot guarantee the stability of the system if a listener goes unregistered
            }
        }

        this.isModifyingEventListenersAdditionsMap = true;
        for (Class<? extends Event> clazz : events) {
            this.pendingListenerAdditionsMap.put(clazz, listener);
        }
        this.isModifyingEventListenersAdditionsMap = false;
        this.pendingListenerAdditionsUpdate = true;
    }

    @Override
    public void removeListener(IEventListener listener) {
        // Cannot remove listeners when the event thread is shutting down
        if (this.isShuttingDown) {
            return;
        }

        List<Class<? extends Event>> events = listener.getEventsToListenFor();

        while (this.isModifyingEventListenerRemovalMap) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                this.engine.error("Pending listener removal in event thread was interrupted", null);
                this.shutdown(true); // Cannot guarantee the stability of the system if a listener goes unregistered
            }
        }

        this.isModifyingEventListenerRemovalMap = true;
        for (Class<? extends Event> clazz : events) {
            this.pendingListenerRemovalMap.put(clazz, listener);
        }
        this.isModifyingEventListenerRemovalMap = false;
        this.pendingListenerRemovalsUpdate = true;
    }

    @Override
    public void postEvent(Event event) {
        // Cannot post new events when the event thread is shutting down
        if (this.isShuttingDown) {
            return;
        }

        while (this.isModifyingEventsList) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                this.engine.error("Pending event posting in event thread was interrupted", null);
                this.shutdown(true); // Cannot guarantee the stability of the system if a listener goes unregistered
            }
        }

        this.isModifyingEventsList = true;
        this.pendingEventList.add(event);
        this.isModifyingEventsList = false;
        this.pendingEventUpdates = true;
    }
}
