package jonathansmith.dpad.client.engine.executor.pluginruntime;

import java.util.HashSet;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.plugins.data.Dataset;
import jonathansmith.dpad.api.plugins.display.IPluginDisplay;
import jonathansmith.dpad.api.plugins.events.IEventThread;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.dpad.common.engine.state.DatasetAdministationState;
import jonathansmith.dpad.common.engine.state.ExperimentAdministrationState;
import jonathansmith.dpad.common.network.packet.play.dataset.DatasetAdministrationPacket;
import jonathansmith.dpad.common.network.packet.play.experiment.ExperimentAdministrationPacket;
import jonathansmith.dpad.common.network.packet.play.plugin.DatasetPacket;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.home.ProgressbarDisplay;
import jonathansmith.dpad.client.gui.pluginruntime.PluginDisplay;

/**
 * Created by Jon on 29/09/2014.
 */
public class PluginRuntime implements IPluginRuntime {

    private final ClientEngine engine;

    public PluginRuntime(ClientEngine engine) {
        this.engine = engine;
    }

    @Override
    public void changeDisplay(IPluginDisplay pluginDisplay) {
        this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new PluginDisplay(this.engine, pluginDisplay)));
    }

    @Override
    public IEventThread getEventThread() {
        return this.engine.getEventThread();
    }

    @Override
    public void buildProgressbarDisplay() {
        this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ProgressbarDisplay(this.engine)));
    }

    @Override
    public void error(String errorMessage) {
        this.engine.error(errorMessage, null);
    }

    @Override
    public void sumbitDataset(Dataset dataset) {
        this.engine.getSession().scheduleOutboundPacket(new DatasetPacket(dataset), new GenericFutureListener[0]);
    }

    @Override
    public void getAvailableExperiments() {
        this.engine.getSession().scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.REQUESTING_EXPERIMENTS), new GenericFutureListener[0]);
    }

    @Override
    public void getDatasetsForExperiments(HashSet<ExperimentRecord> interestedRecords) {
        this.engine.getSession().scheduleOutboundPacket(new DatasetAdministrationPacket(DatasetAdministationState.REQUESTING_EXPERIMENT_DATASETS, interestedRecords), new GenericFutureListener[0]);
    }

    @Override
    public void getFullDatasetInformation(HashSet<DatasetRecord> lazyLoadedDatasets) {
        this.engine.getSession().scheduleOutboundPacket(new DatasetAdministrationPacket(DatasetAdministationState.GET_FULL_DATA, lazyLoadedDatasets), new GenericFutureListener[0]);
    }
}
