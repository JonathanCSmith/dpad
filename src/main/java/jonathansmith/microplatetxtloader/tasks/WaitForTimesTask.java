package jonathansmith.microplatetxtloader.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;
import jonathansmith.microplatetxtloader.events.MicroplateLoaderFirstStageFinishEvent;

/**
 * Created by Jon on 29/09/2014.
 */
public class WaitForTimesTask implements IPluginTask, IEventListener {

    private static final ArrayList<Class<? extends Event>> EVENTS    = new ArrayList<Class<? extends Event>>();
    private static final String                            TASK_NAME = "Wait for times and files";

    static {
        EVENTS.add(MicroplateLoaderFirstStageFinishEvent.class);
    }

    private final MicroplateTXTLoader core;

    private boolean isWaiting = true;
    private boolean isKilled  = false;

    private LinkedList<File>    files;
    private LinkedList<Integer> times;

    public WaitForTimesTask(MicroplateTXTLoader microplateTXTLoader) {
        this.core = microplateTXTLoader;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime engine) {
        if (this.core.isQuittingEarly()) {
            return;
        }

        engine.getEventThread().addEventListener(this);
        while (this.isWaiting && !this.isKilled) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {

            }
        }

        engine.getEventThread().removeListener(this);
        this.core.setFiles(this.files);
        this.core.setTimes(this.times);
    }

    @Override
    public void killTask(IPluginRuntime engine) {
        this.isKilled = true;
        this.core.quitEarly();
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        MicroplateLoaderFirstStageFinishEvent evt = (MicroplateLoaderFirstStageFinishEvent) event;
        this.files = evt.getFiles();
        this.times = evt.getTimes();
        this.isWaiting = false;
    }
}
