package jonathansmith.dpad.client.gui;

import java.util.LinkedList;
import java.util.List;

import jonathansmith.dpad.api.events.ModalDialogRequestEvent;
import jonathansmith.dpad.api.plugins.events.Event;

import jonathansmith.dpad.common.gui.EngineTabController;

import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client GUI display tab. Handles display switching and events
 */
public class ClientTabController extends EngineTabController<ClientDisplay> {

    private static final String                       TITLE  = "DPAD Client";
    private static final List<Class<? extends Event>> EVENTS = new LinkedList<Class<? extends Event>>();

    public ClientTabController() {
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
                ClientDisplayChangeEvent evt = (ClientDisplayChangeEvent) event;
                this.setCurrentDisplay(evt.getTargetDisplay());
                break;
            case 1:
                ModalDialogRequestEvent mevt = (ModalDialogRequestEvent) event;
                this.getCurrentDisplay().getDisplayComponent().showModal(mevt.getModalContent());
                break;
        }
    }

    @Override
    protected boolean shouldShowLog() {
        return DPAD.getInstance().isDebugging();
    }

    static {
        EVENTS.add(ClientDisplayChangeEvent.class);
        EVENTS.add(ModalDialogRequestEvent.class);
    }
}
