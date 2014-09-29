package jonathansmith.kellycharacterisationanalysis.tasks;

import java.util.ArrayList;
import java.util.List;

import jonathansmith.dpad.api.events.dataset.FullDatasetsArrivalEvent;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.events.KellyCharacterisationFinishEvent;

/**
 * Created by Jon on 29/09/2014.
 */
public class RequestAndWaitForDatasetsTask implements IPluginTask, IEventListener {

    private static final ArrayList<Class<? extends Event>> EVENTS    = new ArrayList<Class<? extends Event>>();
    private static final String                            TASK_NAME = "Wait for datasets";

    static {
        EVENTS.add(KellyCharacterisationFinishEvent.class);
        EVENTS.add(FullDatasetsArrivalEvent.class);
    }

    private final KellyCharacterisationAnalysis core;

    private boolean isWaiting = true;
    private boolean isKilled  = false;

    public RequestAndWaitForDatasetsTask(KellyCharacterisationAnalysis kellyCharacterisationAnalysis) {
        this.core = kellyCharacterisationAnalysis;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime runtime) {
        if (this.core.isQuittingEarly()) {
            return;
        }

        runtime.getEventThread().addEventListener(this);
        runtime.getFullDatasetInformation(this.core.getLazyLoadedDatasets());
        while (this.isWaiting && !this.isKilled) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {

            }
        }
        runtime.getEventThread().removeListener(this);
    }

    @Override
    public void killTask(IPluginRuntime runtime) {
        this.isKilled = true;
        this.core.quitEarly();
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return null;
    }

    @Override
    public void onEventReceived(Event event) {
        this.isWaiting = false;
        if (event instanceof FullDatasetsArrivalEvent) {
            this.core.setFullDatasets(((FullDatasetsArrivalEvent) event).getDatasets());
        }

        else if (event instanceof KellyCharacterisationFinishEvent) {
            this.core.quitEarly();
        }
    }
}
