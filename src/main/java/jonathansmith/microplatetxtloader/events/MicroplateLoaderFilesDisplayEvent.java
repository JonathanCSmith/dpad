package jonathansmith.microplatetxtloader.events;

import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 29/09/2014.
 */
public class MicroplateLoaderFilesDisplayEvent extends Event {

    private final DisplayStatus status;

    public MicroplateLoaderFilesDisplayEvent(DisplayStatus status) {
        this.status = status;
    }

    public DisplayStatus getStatus() {
        return this.status;
    }

    public enum DisplayStatus {
        REMOVE_FILES, ADD_FILES
    }
}
