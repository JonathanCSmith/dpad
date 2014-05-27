package jonathansmith.dpad.common.engine.event.gui;

import jonathansmith.dpad.common.engine.event.Event;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Updates any progressbars running in the current engine.
 */
public class ProgressBarUpdateEvent extends Event {

    private final String progress_text;
    private final int    progress_minimum;
    private final int    progress_maximum;
    private final int    progress;

    public ProgressBarUpdateEvent(String text, int minimum, int maximum, int currentValue) {
        this.progress_text = text;
        this.progress_minimum = minimum;
        this.progress_maximum = maximum;
        this.progress = currentValue;
    }

    public String getProgressBarText() {
        return this.progress_text;
    }

    public int getProgressMinimum() {
        return this.progress_minimum;
    }

    public int getProgressMaximum() {
        return this.progress_maximum;
    }

    public int getProgress() {
        return this.progress;
    }
}
