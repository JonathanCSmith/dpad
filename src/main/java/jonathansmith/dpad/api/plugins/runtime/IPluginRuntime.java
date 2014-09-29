package jonathansmith.dpad.api.plugins.runtime;

import java.util.HashSet;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.plugins.data.Dataset;
import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.events.IEventThread;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * All accessible methods for plugins
 */
public interface IPluginRuntime {

    void changeDisplay(IPluginDisplay pluginDisplay);

    IEventThread getEventThread();

    void buildProgressbarDisplay();

    void error(String errorMessage);

    void sumbitDataset(Dataset dataset);

    void getAvailableExperiments();

    void getDatasetsForExperiments(HashSet<ExperimentRecord> interestedRecords);

    void getFullDatasetInformation(HashSet<DatasetRecord> lazyLoadedDatasets);
}
