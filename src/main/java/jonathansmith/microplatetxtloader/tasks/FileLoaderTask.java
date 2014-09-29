package jonathansmith.microplatetxtloader.tasks;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import jonathansmith.dpad.api.events.ModalDialogRequestEvent;
import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;
import jonathansmith.microplatetxtloader.data.MicroplateData;

/**
 * Created by Jon on 29/09/2014.
 */
public class FileLoaderTask implements IPluginTask {

    private static final String TASK_NAME = "Load Files";

    private final MicroplateTXTLoader core;

    private boolean isKilled = false;

    public FileLoaderTask(MicroplateTXTLoader microplateTXTLoader) {
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

        engine.buildProgressbarDisplay();
        LinkedList<File> files = this.core.getFiles();
        LinkedList<Integer> times = this.core.getTimes();
        engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Beginning file loading, this may take some time", 0, files.size() + 1, 0));

        LinkedList<MicroplateData> data = new LinkedList<MicroplateData>();
        String dataSource = "";
        int height = -1;
        int width = -1;
        for (int i = 0; i < files.size(); i++) {
            if (this.isKilled) {
                return;
            }

            boolean worked;
            MicroplateData info = new MicroplateData();
            try {
                worked = info.buildFromFile(files.get(i), times.get(i));
            }

            catch (IOException e) {
                engine.getEventThread().postEvent(new ModalDialogRequestEvent("Failure to load a file. The plugin failed!"));
                this.core.quitEarly();
                return;
            }

            if (!worked) {
                engine.getEventThread().postEvent(new ModalDialogRequestEvent("Some of your data is not the same size microplate. The plugin failed!"));
                this.core.quitEarly();
                return;
            }

            if (dataSource.contentEquals("")) {
                dataSource = info.getSampleMeasurementCondition();
            }

            else if (!dataSource.contentEquals(info.getSampleMeasurementCondition())) {
                engine.getEventThread().postEvent(new ModalDialogRequestEvent("Your data has different excitation/emmission values... The plugin failed!"));
                this.core.quitEarly();
                return;
            }

            data.add(info);
            if (height == -1) {
                height = info.getHeight();
            }

            else if (height != info.getHeight()) {
                engine.getEventThread().postEvent(new ModalDialogRequestEvent("Your data is not all on the same sized microplate! The plugin failed!"));
                this.core.quitEarly();
                return;
            }

            if (width == -1) {
                width = info.getWidth();
            }

            else if (width != info.getWidth()) {
                engine.getEventThread().postEvent(new ModalDialogRequestEvent("Your data is not all on the same sized microplate! The plugin failed"));
                this.core.quitEarly();
                return;
            }

            engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Loaded files: ", 0, files.size(), i));
        }

        if (!this.core.isKnownMicroplateSize(data.get(0).getHeight() * data.get(0).getWidth())) {
            engine.getEventThread().postEvent(new ModalDialogRequestEvent("Your data does not correspond to a known microplate size! The plugin failed"));
            this.core.quitEarly();
            return;
        }

        else {
            this.core.setMicroplateHeight(data.get(0).getHeight());
            this.core.setMicroplateWidth(data.get(0).getWidth());
        }

        this.core.setMicroplateData(data);
    }

    @Override
    public void killTask(IPluginRuntime engine) {
        this.core.quitEarly();
        this.isKilled = true;
    }
}
