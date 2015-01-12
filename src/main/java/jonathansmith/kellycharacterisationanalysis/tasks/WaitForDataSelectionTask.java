package jonathansmith.kellycharacterisationanalysis.tasks;

import java.util.ArrayList;
import java.util.List;

import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.events.KellySelectDataFinishEvent;

/**
 * Created by Jon on 29/09/2014.
 */
public class WaitForDataSelectionTask implements IPluginTask, IEventListener {

    private static final ArrayList<Class<? extends Event>> EVENTS    = new ArrayList<Class<? extends Event>>();
    private static final String                            TASK_NAME = "Wait for data selection";

    static {
        EVENTS.add(KellySelectDataFinishEvent.class);
    }

    private final KellyCharacterisationAnalysis core;

    private boolean isWaiting = true;
    private boolean isKilled  = false;

    public WaitForDataSelectionTask(KellyCharacterisationAnalysis kellyCharacterisationAnalysis) {
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
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        this.isWaiting = false;
        if (((KellySelectDataFinishEvent) event).getInterestedRecords() == null) {
            this.core.quitEarly();
        }

        else {
            this.core.setLazyLoadedDatasets(((KellySelectDataFinishEvent) event).getInterestedRecords());
        }
    }
}
