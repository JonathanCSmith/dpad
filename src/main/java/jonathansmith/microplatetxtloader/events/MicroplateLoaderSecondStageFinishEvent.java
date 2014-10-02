package jonathansmith.microplatetxtloader.events;

import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 29/09/2014.
 */
public class MicroplateLoaderSecondStageFinishEvent extends Event {

    private final String[][] values;

    public MicroplateLoaderSecondStageFinishEvent(String[][] values) {
        this.values = values;
    }

    public String[][] getValues() {
        return this.values;
    }
}
