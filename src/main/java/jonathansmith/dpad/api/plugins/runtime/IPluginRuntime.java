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

    /**
     * Change the current display within the plugin runtime environment
     *
     * @param pluginDisplay
     */
    void changeDisplay(IPluginDisplay pluginDisplay);

    /**
     * Return the main event thread for the plugin environment
     *
     * @return
     */
    IEventThread getEventThread();

    /**
     * Build standard progressbar display
     */
    void buildProgressbarDisplay();

    /**
     * Record an error to the engine
     *
     * @param errorMessage
     */
    void error(String errorMessage);

    /**
     * Submit a dataset to the server
     *
     * @param dataset
     */
    void sumbitDataset(Dataset dataset);

    /**
     * Obtain experiments available to the user
     */
    void getAvailableExperiments();

    /**
     * Return lazy loaded datasets (do not contain full information)
     *
     * @param interestedRecords
     */
    void getDatasetsForExperiments(HashSet<ExperimentRecord> interestedRecords);

    /**
     * Fully load datasets
     *
     * @param lazyLoadedDatasets
     */
    void getFullDatasetInformation(HashSet<DatasetRecord> lazyLoadedDatasets);
}
