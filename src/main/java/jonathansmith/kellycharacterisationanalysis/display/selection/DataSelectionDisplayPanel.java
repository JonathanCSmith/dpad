package jonathansmith.kellycharacterisationanalysis.display.selection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.events.dataset.DatasetsArrivalEvent;
import jonathansmith.dpad.api.events.dataset.ServerExperimentResponseEvent;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.events.KellySelectDataFinishEvent;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for all GUI displays
 */
public class DataSelectionDisplayPanel extends DisplayPanel implements IEventListener, ActionListener {

    private static final ArrayList<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(KellySelectDataFinishEvent.class);
        EVENTS.add(ServerExperimentResponseEvent.ExperimentRecordsResponse.class);
        EVENTS.add(DatasetsArrivalEvent.class);
    }

    private final KellyCharacterisationAnalysis core;
    private final IPluginRuntime                runtime;

    private State state = State.EXPERIMENTS;

    private StringSelectionTableModel      singleModel;
    private MultiStringSelectionTableModel multiModel;
    private HashSet                        records;

    private JPanel     contentPane;
    private JTextField infoText;
    private JTable     table;
    private JButton    finishButton;

    public DataSelectionDisplayPanel(KellyCharacterisationAnalysis core, IPluginRuntime runtime) {
        this.core = core;
        this.runtime = runtime;

        this.table.setVisible(false);
        this.singleModel = new StringSelectionTableModel();
        this.multiModel = new MultiStringSelectionTableModel();
        this.table.setModel(this.singleModel);

        this.finishButton.addActionListener(this);
        this.finishButton.setVisible(false);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    public void onActivate() {
        this.runtime.getAvailableExperiments();
    }

    @Override
    public void update() {

    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        if (event instanceof KellySelectDataFinishEvent) {
            this.finishButton.setEnabled(false);
        }

        else if (event instanceof ServerExperimentResponseEvent.ExperimentRecordsResponse) {
            ServerExperimentResponseEvent.ExperimentRecordsResponse response = (ServerExperimentResponseEvent.ExperimentRecordsResponse) event;
            this.records = response.getExperimentRecords();

            LinkedList<String> names = new LinkedList<String>();
            for (Object record : this.records) {
                names.add(((ExperimentRecord) record).getExperimentName());
            }

            this.singleModel.setData(names);
            this.table.setVisible(true);
            this.infoText.setText("Please select the experiments that are of interest to you:");
            this.finishButton.setVisible(true);
        }

        else if (event instanceof DatasetsArrivalEvent) {
            DatasetsArrivalEvent datasetResponse = (DatasetsArrivalEvent) event;
            this.records = datasetResponse.getDatasets();

            LinkedList<String> names = new LinkedList<String>();
            LinkedList<String> measurement = new LinkedList<String>();
            LinkedList<String> plugins = new LinkedList<String>();
            for (Object record : this.records) {
                DatasetRecord dataset = (DatasetRecord) record;
                names.add(dataset.getSampleRecord().getSampleName());
                measurement.add(dataset.getMeasurementType().getMeasurementType());
                plugins.add(dataset.getPluginSource().getPluginName());
            }

            this.infoText.setText("Please select the datasets that are of interest to you:");
            this.table.setVisible(true);
            this.table.setModel(this.multiModel);
            this.multiModel.setData(names, measurement, plugins);
            this.finishButton.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.finishButton) {
            if (this.state == State.EXPERIMENTS) {
                LinkedList<String> names = this.singleModel.getSelectedNames();
                if (names.isEmpty()) {
                    this.runtime.getEventThread().postEvent(new KellySelectDataFinishEvent(null));
                    this.core.quitEarly();
                    this.showModal("No experiments were chosen!");
                    return;
                }

                HashSet<ExperimentRecord> interestedRecords = new HashSet<ExperimentRecord>();
                for (String name : names) {
                    for (Object record : this.records) {
                        if (((ExperimentRecord) record).getExperimentName().contentEquals(name)) {
                            interestedRecords.add((ExperimentRecord) record);
                        }
                    }
                }

                this.runtime.getDatasetsForExperiments(interestedRecords);
                this.state = State.DATASETS;
                this.table.setVisible(false);
                this.infoText.setText("Awaiting information from the server...");
                this.finishButton.setVisible(false);
            }

            else if (this.state == State.DATASETS) {
                LinkedList<String> samples = this.multiModel.getSelectedSamples();
                HashSet<DatasetRecord> interestedRecords = new HashSet<DatasetRecord>();
                for (String name : samples) {
                    for (Object record : this.records) {
                        if (((DatasetRecord) record).getSampleRecord().getSampleName().contentEquals(name)) {
                            interestedRecords.add((DatasetRecord) record);
                        }
                    }
                }

                this.runtime.getEventThread().postEvent(new KellySelectDataFinishEvent(interestedRecords));
            }
        }
    }

    private enum State {
        EXPERIMENTS,
        DATASETS
    }
}
