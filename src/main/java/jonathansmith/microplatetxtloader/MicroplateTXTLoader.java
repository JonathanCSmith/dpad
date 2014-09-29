package jonathansmith.microplatetxtloader;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import jonathansmith.dpad.api.plugins.ILoaderPlugin;
import jonathansmith.dpad.api.plugins.records.ILoaderPluginRecord;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.microplatetxtloader.data.MicroplateData;
import jonathansmith.microplatetxtloader.tasks.*;

/**
 * Created by Jon on 29/09/2014.
 */
@PluginImplementation
public class MicroplateTXTLoader implements ILoaderPlugin {

    private static final ArrayList<Integer> MICROPLATE_TYPES = new ArrayList<Integer>();

    static {
        MICROPLATE_TYPES.add(6);
        MICROPLATE_TYPES.add(24);
        MICROPLATE_TYPES.add(96);
        MICROPLATE_TYPES.add(384);
        MICROPLATE_TYPES.add(1536);
    }

    private final LinkedList<IPluginTask> tasks_list = new LinkedList<IPluginTask>();

    private final ILoaderPluginRecord plugin_record;

    private boolean isQuittingWithoutLoading = false;

    private LinkedList<File>           files;
    private LinkedList<Integer>        times;
    private LinkedList<MicroplateData> rawData;
    private int                        microplateHeight;
    private int                        microplateWidth;
    private String[][]                 dataMask;

    public MicroplateTXTLoader() {
        this.plugin_record = new MicroplateTXTLoaderPluginRecord();
        this.addTasks();
    }

    private void addTasks() {
        this.tasks_list.add(new BuildFirstDisplayTask(this));
        this.tasks_list.add(new WaitForTimesTask(this));
        this.tasks_list.add(new FileLoaderTask(this));
        this.tasks_list.add(new BuildSecondDisplayTask(this));
        this.tasks_list.add(new WaitForMetadata(this));
        this.tasks_list.add(new FinishTasksTask(this));
    }

    @Override
    public ILoaderPluginRecord getPluginRecord() {
        return this.plugin_record;
    }

    @Override
    public LinkedList<IPluginTask> getPluginRuntimeTasks() {
        return this.tasks_list;
    }

    public void quitEarly() {
        this.isQuittingWithoutLoading = true;
    }

    public boolean isQuittingEarly() {
        return this.isQuittingWithoutLoading;
    }

    public LinkedList<File> getFiles() {
        return this.files;
    }

    public void setFiles(LinkedList<File> files) {
        this.files = files;
    }

    public LinkedList<Integer> getTimes() {
        return this.times;
    }

    public void setTimes(LinkedList<Integer> times) {
        this.times = times;
    }

    public LinkedList<MicroplateData> getMicroplateData() {
        return this.rawData;
    }

    public void setMicroplateData(LinkedList<MicroplateData> data) {
        this.rawData = data;
    }

    public boolean isKnownMicroplateSize(int numberOfWells) {
        return MICROPLATE_TYPES.contains(numberOfWells);
    }

    public int getMicroplateHeight() {
        return this.microplateHeight;
    }

    public void setMicroplateHeight(int microplateHeight) {
        this.microplateHeight = microplateHeight;
    }

    public int getMicroplateWidth() {
        return this.microplateWidth;
    }

    public void setMicroplateWidth(int microplateWidth) {
        this.microplateWidth = microplateWidth;
    }

    public String[][] getDataMask() {
        return dataMask;
    }

    public void setDataMask(String[][] dataMask) {
        this.dataMask = dataMask;
    }
}
