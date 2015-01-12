package jonathansmith.kellycharacterisationanalysis.display.assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.data.KellyDeconvolutionMask;
import jonathansmith.kellycharacterisationanalysis.events.KellyDataAssignmentFinishEvent;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for all GUI displays
 */
public class DataAssignmentDisplayPanel extends DisplayPanel implements IEventListener, ActionListener {

    private static final ArrayList<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(KellyDataAssignmentFinishEvent.class);
    }

    private final KellyCharacterisationAnalysis core;
    private final IPluginRuntime                runtime;

    private State state = State.GATHER_INFO;

    private DataAssignmentTableModel tableModel;
    private int                      numberOfSamples;
    private int                      numberOfIndependentRepeats;
    private int                      numberOfDependentRepeats;
    private HashSet<DatasetRecord>   records;

    private JPanel      contentPane;
    private JTextField  pleaseEnterTheRequiredTextField;
    private JTextField  numberOfIndependentRepeatsTextField;
    private JTextField  independents;
    private JTextField  numberOfDependentRepeatsTextField;
    private JTextField  dependents;
    private JScrollPane scrollPane;
    private JTable      table1;
    private JButton     submitButton;
    private JTextField  numberOfSamplesToTextField;
    private JTextField  samples;
    ;

    public DataAssignmentDisplayPanel(KellyCharacterisationAnalysis core, IPluginRuntime runtime) {
        this.core = core;
        this.runtime = runtime;

        this.scrollPane.setVisible(false);
        this.table1.setVisible(false);
        this.tableModel = new DataAssignmentTableModel();
        this.table1.setModel(this.tableModel);
        this.submitButton.addActionListener(this);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
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
        if (event instanceof KellyDataAssignmentFinishEvent) {
            this.submitButton.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.submitButton) {
            switch (this.state) {
                case GATHER_INFO:
                    int samples;
                    int independent;
                    int dependent;
                    try {
                        samples = Integer.parseInt(this.samples.getText());
                        independent = Integer.parseInt(this.independents.getText());
                        dependent = Integer.parseInt(this.dependents.getText());
                    }

                    catch (NumberFormatException ex) {
                        this.showModal("Some of your inputs could not be identified as numbers. Please check that the information in all fields is correct!");
                        return;
                    }

                    if (samples <= 0 || independent <= 0 || dependent <= 0) {
                        this.showModal("None of the fields can be less than or equal to 0!");
                        return;
                    }

                    this.numberOfSamples = samples;
                    this.numberOfIndependentRepeats = independent;
                    this.numberOfDependentRepeats = dependent;

                    this.records = this.core.getLazyLoadedDatasets();
                    int numberRequired = this.numberOfSamples * this.numberOfDependentRepeats * this.numberOfIndependentRepeats;
                    if (numberRequired != this.records.size()) {
                        this.showModal("You need: " + numberRequired + " datasets to perform the characterisation you are specifying. There are either too many or too little that have been selected.");
                        return;
                    }

                    this.state = State.ASSIGN_INFO;
                    this.pleaseEnterTheRequiredTextField.setText("Assign the respective information to the datasets below: ");
                    this.numberOfSamplesToTextField.setVisible(false);
                    this.samples.setVisible(false);
                    this.numberOfIndependentRepeatsTextField.setVisible(false);
                    this.independents.setVisible(false);
                    this.numberOfDependentRepeatsTextField.setVisible(false);
                    this.dependents.setVisible(false);

                    this.scrollPane.setVisible(true);

                    String[] sampleList = new String[this.numberOfSamples];
                    for (int i = 1; i <= this.numberOfSamples; i++) {
                        sampleList[i - 1] = String.valueOf(i);
                    }

                    String[] independentRepeatList = new String[this.numberOfIndependentRepeats];
                    for (int i = 1; i <= this.numberOfIndependentRepeats; i++) {
                        independentRepeatList[i - 1] = String.valueOf(i);
                    }

                    String[] dependentRepeatList = new String[this.numberOfDependentRepeats];
                    for (int i = 1; i <= this.numberOfDependentRepeats; i++) {
                        dependentRepeatList[i - 1] = String.valueOf(i);
                    }

                    this.tableModel.setData(this.records);
                    this.table1.setDefaultEditor(String.class, new DataAssignmentTableEditor(sampleList, independentRepeatList, dependentRepeatList));
                    this.table1.setVisible(true);
                    break;

                case ASSIGN_INFO:
                    LinkedList<KellyDeconvolutionMask> masks = this.tableModel.getMasks();
                    LinkedList<KellyDeconvolutionMask> orderedMasks = new LinkedList<KellyDeconvolutionMask>();
                    boolean isFinished = false;
                    int sampleNumber = 1;
                    int independentNumber = 1;
                    int dependentNumber = 1;
                    int potentialReference = -1;
                    while (!isFinished) {
                        boolean isCompleteRun = true;
                        for (KellyDeconvolutionMask mask : masks) {
                            if (mask.getSampleNumber() == sampleNumber) {
                                if (mask.getIndependentRepeatNumber() == independentNumber) {
                                    if (mask.getDependentRepeatNumber() == dependentNumber) {
                                        if (sampleNumber == this.numberOfSamples
                                                && independentNumber == this.numberOfIndependentRepeats
                                                && dependentNumber == this.numberOfDependentRepeats
                                                && potentialReference != -1) {
                                            isFinished = true;
                                        }

                                        if (mask.isReferenceSample()
                                                && (potentialReference == -1 || potentialReference == mask.getSampleNumber())) {
                                            potentialReference = mask.getSampleNumber();
                                        }

                                        else if (potentialReference != -1 && potentialReference != mask.getSampleNumber()) {
                                            this.showModal("You seem to have multiple sample numbers set as a reference. Only one sample NUMBER should correspond to the standard reference");
                                            return;
                                        }

                                        orderedMasks.add(mask);
                                        isCompleteRun = false;
                                        dependentNumber++;
                                    }

                                    if (!isCompleteRun && dependentNumber == this.numberOfDependentRepeats + 1) {
                                        independentNumber++;
                                        dependentNumber = 1;
                                    }
                                }

                                if (!isCompleteRun && independentNumber == this.numberOfIndependentRepeats + 1) {
                                    sampleNumber++;
                                    independentNumber = 1;
                                }
                            }

                            if (!isCompleteRun) {
                                break;
                            }
                        }

                        if (isCompleteRun) {
                            this.showModal("Could not find sample corresponding to sample number" + sampleNumber + " independent repeat number " + independentNumber + " dependent repeat number " + dependentNumber);
                            return;
                        }
                    }

                    this.runtime.getEventThread().postEvent(new KellyDataAssignmentFinishEvent(orderedMasks));
                    break;
            }
        }
    }

    private enum State {
        GATHER_INFO,
        ASSIGN_INFO
    }
}
