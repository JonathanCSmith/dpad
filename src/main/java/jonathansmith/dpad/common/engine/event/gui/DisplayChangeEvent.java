package jonathansmith.dpad.common.engine.event.gui;

import jonathansmith.dpad.common.engine.event.Event;
import jonathansmith.dpad.common.gui.display.Display;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Event used when displays are changed
 */
public abstract class DisplayChangeEvent extends Event {

    private final Display targetDisplay;

    public DisplayChangeEvent(Display display) {
        this.targetDisplay = display;
    }

    public Display getTargetDisplay() {
        return this.targetDisplay;
    }
}
