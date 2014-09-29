package jonathansmith.dpad.client.engine.executor.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.events.ModalDialogRequestEvent;
import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;
import jonathansmith.dpad.api.events.dataset.ServerExperimentResponseEvent;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.experiment.ExperimentAdministrationPanel;
import jonathansmith.dpad.client.gui.experiment.ExperimentDisplay;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Task to wait for the server to respond with the list of available experiments
 */
public class ExperimentResponseWaitTask extends Task implements IEventListener {

    private static final String                       TASK_NAME = "Await server response";
    private static final List<Class<? extends Event>> EVENTS    = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(ServerExperimentResponseEvent.class);
        EVENTS.add(ServerExperimentResponseEvent.StateResponse.class);
        EVENTS.add(ServerExperimentResponseEvent.ExperimentResponse.class);
        EVENTS.add(ServerExperimentResponseEvent.ExperimentRecordsResponse.class);
    }

    private final ExperimentDisplay display;

    private boolean isWaiting = true;
    private boolean isKilled  = false;
    private ServerExperimentResponseEvent event;

    public ExperimentResponseWaitTask(Engine engine, ExperimentDisplay display) {
        super(TASK_NAME, engine);

        this.display = display;
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().addEventListener(this);
        while (this.isWaiting && !this.isKilled) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                // No matter - we don't need to pass this up
            }
        }

        this.loggingEngine.getEventThread().removeListener(this);
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Data Received", 0, 2, 1));

        switch (this.event.getResponseType()) {
            case NOT_LOGGED_IN:
                this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Rebuilding experiment display", 0, 2, 2));
                this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(this.display));
                this.loggingEngine.getEventThread().addEventListener((IEventListener) this.display.getDisplayComponent());
                this.loggingEngine.getEventThread().postEvent(new ModalDialogRequestEvent("You are not currently logged in. This is an error"));
                this.display.getDisplayComponent().showModal("Invalid Session. It is advised that you restart the program");
                break;

            case EXPERIMENT_NAME_NOT_UNIQUE:
                this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Rebuilding experiment display", 0, 2, 2));
                this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(this.display));
                this.loggingEngine.getEventThread().addEventListener((IEventListener) this.display.getDisplayComponent());
                this.loggingEngine.getEventThread().postEvent(new ModalDialogRequestEvent("Your experiment name is not unique among your current experiments!"));
                this.display.getDisplayComponent().showModal("Invalid experiment name. It is not the first time you have used this name!");
                break;

            case EXPERIMENT_CREATION_SUCCESS:
                this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Rebuilding experiment display", 0, 2, 2));
                this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(this.display));
                this.loggingEngine.getEventThread().postEvent(new ModalDialogRequestEvent("The experiment was created successfully"));
                this.loggingEngine.getEventThread().addEventListener((IEventListener) this.display.getDisplayComponent());
                break;

            case CANNOT_FIND_PROVIDED_EXPERIMENT:
                this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Rebuilding experiment display", 0, 2, 2));
                this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(this.display));
                this.loggingEngine.getEventThread().postEvent(new ModalDialogRequestEvent("The selected experiment was not found"));
                this.loggingEngine.getEventThread().addEventListener((IEventListener) this.display.getDisplayComponent());
                this.display.getDisplayComponent().showModal("Could not find the provided experiment. Please try loading again.");
                break;

            case EXPERIMENT_SELECTION_SUCCESS:
                this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Rebuilding experiment display", 0, 2, 2));
                this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(this.display));
                this.loggingEngine.getEventThread().addEventListener((IEventListener) this.display.getDisplayComponent());
                break;

            case SENDING_EXPERIMENTS:
                ServerExperimentResponseEvent.ExperimentRecordsResponse response = (ServerExperimentResponseEvent.ExperimentRecordsResponse) this.event;
                HashSet<ExperimentRecord> experiments = response.getExperimentRecords();
                ((ExperimentAdministrationPanel) this.display.getDisplayComponent()).setListContents(experiments);
                this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Rebuilding experiment display", 0, 2, 2));
                this.loggingEngine.getEventThread().postEvent(new ClientDisplayChangeEvent(this.display));
                this.loggingEngine.getEventThread().addEventListener((IEventListener) this.display.getDisplayComponent());
                break;
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        this.event = (ServerExperimentResponseEvent) event;
        this.isWaiting = false;
    }
}
