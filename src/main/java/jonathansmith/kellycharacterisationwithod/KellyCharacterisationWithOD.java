package jonathansmith.kellycharacterisationwithod;

import java.util.LinkedList;

import jonathansmith.dpad.api.plugins.IAnalyserPlugin;
import jonathansmith.dpad.api.plugins.records.IAnalyserPluginRecord;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationwithod.tasks.DisplayDataSelectionTask;

/**
 * Created by Jon on 09/11/2014.
 */
public class KellyCharacterisationWithOD implements IAnalyserPlugin {

    private final LinkedList<IPluginTask> task_list = new LinkedList<IPluginTask>();

    private final IAnalyserPluginRecord plugin_record;

    public KellyCharacterisationWithOD() {
        this.plugin_record = new KellyCharacterisationWithODPluginRecord();
        this.addTasks();
    }

    private void addTasks() {
        this.task_list.add(new DisplayDataSelectionTask(this));
        // Data selection
        // Wait for data selection
        // Data assignment
        // Wait for data assignment
        // Characterisation display task
        // request and wait for datasets
        // calculate characterisdation
        // wait for finish
    }

    @Override
    public IAnalyserPluginRecord getPluginRecord() {
        return this.plugin_record;
    }

    @Override
    public LinkedList<IPluginTask> getPluginRuntimeTasks() {
        return this.task_list;
    }

    public void quitEarly() {

    }
}
