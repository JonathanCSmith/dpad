package jonathansmith.dpad.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.common.engine.event.Event;
import jonathansmith.dpad.common.gui.EngineTabController;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client GUI display tab. Handles display switching and events
 */
public class ClientTabController extends EngineTabController {

    private static final String                       TITLE  = "DPAD Client";
    private static final List<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>(
            Arrays.asList(
                    ClientDisplayChangeEvent.class
            )
    );

    private ClientDisplay currentDisplay = null;
    private ClientDisplay oldDisplay     = null;

    public ClientTabController() {
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public void init() {
        super.init();

        this.engine.getEventThread().addEventListener(this);
    }

    @Override
    public void update() {
        super.update();

        if (this.currentDisplay != this.oldDisplay) {
            int dividerLocation = this.coreDisplaySplitPane.getDividerLocation();
            this.coreDisplaySplitPane.setLeftComponent(this.currentDisplay.getToolbarComponent());
            this.coreDisplaySplitPane.setRightComponent(this.currentDisplay.getDisplayComponent());
            this.coreDisplaySplitPane.setDividerLocation(dividerLocation);
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        ClientDisplayChangeEvent evt = (ClientDisplayChangeEvent) event;
        this.currentDisplay = (ClientDisplay) evt.getTargetDisplay();
    }
}
