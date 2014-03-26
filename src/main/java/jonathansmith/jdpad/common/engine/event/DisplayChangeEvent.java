package jonathansmith.jdpad.common.engine.event;

import jonathansmith.jdpad.common.gui.display.Display;

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
