package jonathansmith.kellycharacterisationanalysis;

import java.util.HashSet;
import java.util.LinkedList;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.plugins.IAnalyserPlugin;
import jonathansmith.dpad.api.plugins.records.IAnalyserPluginRecord;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationanalysis.data.KellyDeconvolutionMask;
import jonathansmith.kellycharacterisationanalysis.tasks.*;

/**
 * Created by Jon on 29/09/2014.
 */
@PluginImplementation
public class KellyCharacterisationAnalysis implements IAnalyserPlugin {

    private final LinkedList<IPluginTask> tasks_list = new LinkedList<IPluginTask>();

    private final IAnalyserPluginRecord plugin_record;

    private boolean                            isQuittingEarly;
    private HashSet<DatasetRecord>             lazyLoadedDatasets;
    private LinkedList<KellyDeconvolutionMask> dataMasks;
    private HashSet<DatasetRecord>             fullDatasets;
    private int                                lowerOptimum;
    private int                                upperOptimim;

    public KellyCharacterisationAnalysis() {
        this.plugin_record = new KellyCharacterisationAnalysisPluginRecord();
        this.addTasks();
    }

    private void addTasks() {
        this.tasks_list.add(new DataSelectionDisplayTask(this));
        this.tasks_list.add(new WaitForDataSelectionTask(this));
        this.tasks_list.add(new DataAssignmentDisplayTask(this));
        this.tasks_list.add(new WaitForDataAssignmentTask(this));
        this.tasks_list.add(new CharacterisationDisplayTask(this));
        this.tasks_list.add(new RequestAndWaitForDatasetsTask(this));
        this.tasks_list.add(new CalculateCharacterisationTask(this));
        this.tasks_list.add(new WaitForUserFinish(this));
    }

    @Override
    public IAnalyserPluginRecord getPluginRecord() {
        return this.plugin_record;
    }

    @Override
    public LinkedList<IPluginTask> getPluginRuntimeTasks() {
        return this.tasks_list;
    }

    public void quitEarly() {
        this.isQuittingEarly = true;
    }

    public boolean isQuittingEarly() {
        return this.isQuittingEarly;
    }

    public HashSet<DatasetRecord> getLazyLoadedDatasets() {
        return lazyLoadedDatasets;
    }

    public void setLazyLoadedDatasets(HashSet<DatasetRecord> lazyLoadedDatasets) {
        this.lazyLoadedDatasets = lazyLoadedDatasets;
    }

    public LinkedList<KellyDeconvolutionMask> getDataMasks() {
        return dataMasks;
    }

    public void setDataMasks(LinkedList<KellyDeconvolutionMask> dataMasks) {
        this.dataMasks = dataMasks;
    }

    public HashSet<DatasetRecord> getFullDatasets() {
        return fullDatasets;
    }

    public void setFullDatasets(HashSet<DatasetRecord> fullDatasets) {
        this.fullDatasets = fullDatasets;
    }

    public int getLowerOptimum() {
        return this.lowerOptimum;
    }

    public void setLowerOptimum(int optimum) {
        this.lowerOptimum = optimum;
    }

    public int getUpperOptimim() {
        return upperOptimim;
    }

    public void setUpperOptimim(int upperOptimim) {
        this.upperOptimim = upperOptimim;
    }
}
