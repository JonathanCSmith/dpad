package jonathansmith.dpad.api.events;

import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Allows for event based ok only modal dialogs to be generated
 */
public class ModalDialogRequestEvent extends Event {

    private final String modal_content;

    public ModalDialogRequestEvent(String modalContent) {
        this.modal_content = modalContent;
    }

    public String getModalContent() {
        return this.modal_content;
    }
}
