package jonathansmith.dpad.server.gui;

import java.util.LinkedList;
import java.util.List;

import jonathansmith.dpad.api.events.ModalDialogRequestEvent;
import jonathansmith.dpad.api.plugins.events.Event;

import jonathansmith.dpad.common.gui.EngineTabController;

import jonathansmith.dpad.server.engine.event.ServerDisplayChangeEvent;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client GUI display tab
 */
public class ServerTabController extends EngineTabController<ServerDisplay> {

    private static final String                       TITLE  = "DPAD Server";
    private static final List<Class<? extends Event>> EVENTS = new LinkedList<Class<? extends Event>>();

    public ServerTabController() {
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public void init() {
        this.engine.getEventThread().addEventListener(this);
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        int index = EVENTS.indexOf(event.getClass());
        switch (index) {
            case -1:
                return;
            case 0:
                ServerDisplayChangeEvent evt = (ServerDisplayChangeEvent) event;
                this.setCurrentDisplay(evt.getTargetDisplay());
                break;
            case 1:
                ModalDialogRequestEvent mevt = (ModalDialogRequestEvent) event;
                this.getCurrentDisplay().getDisplayComponent().showModal(mevt.getModalContent());
                break;
        }
    }

    static {
        EVENTS.add(ServerDisplayChangeEvent.class);
        EVENTS.add(ModalDialogRequestEvent.class);
    }

    @Override
    protected boolean shouldShowLog() {
        return true;
    }
}
