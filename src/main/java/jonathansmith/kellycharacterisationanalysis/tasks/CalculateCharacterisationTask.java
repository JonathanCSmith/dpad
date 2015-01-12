package jonathansmith.kellycharacterisationanalysis.tasks;

import java.util.HashSet;
import java.util.LinkedList;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.events.ModalDialogRequestEvent;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.data.DeconvolutedData;
import jonathansmith.kellycharacterisationanalysis.data.KellyDeconvolutionMask;
import jonathansmith.kellycharacterisationanalysis.events.CharacterisationCompleteEvent;
import jonathansmith.kellycharacterisationanalysis.events.DeconvolutionCompleteEvent;
import jonathansmith.kellycharacterisationanalysis.events.RateCalculationCompleteEvent;

/**
 * Created by Jon on 29/09/2014.
 */
public class CalculateCharacterisationTask implements IPluginTask {

    private static final String TASK_NAME = "Calculate Characterisation";

    private final KellyCharacterisationAnalysis core;

    public CalculateCharacterisationTask(KellyCharacterisationAnalysis kellyCharacterisationAnalysis) {
        this.core = kellyCharacterisationAnalysis;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime runtime) {
        if (this.core.isQuittingEarly()) {
            return;
        }

        // Link data to masks
        HashSet<DatasetRecord> records = this.core.getFullDatasets();
        LinkedList<KellyDeconvolutionMask> masks = this.core.getDataMasks();
        int maxSample = -1;
        int dependentNumber = -1;
        int independentNumber = -1;
        for (KellyDeconvolutionMask mask : masks) {
            DatasetRecord data = null;
            for (DatasetRecord record : records) {
                if (record.getSampleRecord().getSampleName().contentEquals(mask.getName())
                        && record.getMeasurementType().getMeasurementType().contentEquals(mask.getMeasurementDetails())
                        && record.getPluginSource().getPluginName().contentEquals(mask.getPluginSource())) {
                    data = record;
                }
            }

            if (data == null) {
                runtime.getEventThread().postEvent(new ModalDialogRequestEvent("Could not link a mask to its dataset. Plugin failure"));
                this.core.quitEarly();
                return;
            }

            if (mask.getSampleNumber() > maxSample) {
                maxSample = mask.getSampleNumber();
            }

            if (mask.getDependentRepeatNumber() > dependentNumber) {
                dependentNumber = mask.getDependentRepeatNumber();
            }

            if (mask.getIndependentRepeatNumber() > independentNumber) {
                independentNumber = mask.getIndependentRepeatNumber();
            }

            mask.setData(data.getMeasurements());
        }

        // Format data to be useful
        LinkedList<DeconvolutedData> dataList = new LinkedList<DeconvolutedData>();
        int dataReference = -1;
        boolean newSample = true;
        for (int i = 0; i < maxSample; i++) {
            DeconvolutedData data = new DeconvolutedData(i, independentNumber, dependentNumber);
            for (int j = 0; j < independentNumber; j++) {
                for (int k = 0; k < dependentNumber; k++) {
                    for (KellyDeconvolutionMask mask : masks) {
                        if (mask.getSampleNumber() == i + 1
                                && mask.getIndependentRepeatNumber() == j + 1
                                && mask.getDependentRepeatNumber() == k + 1) {

                            data.setGFPValues(j, k, mask.getData());

                            if (mask.isReferenceSample()) {
                                data.setIsReference(true);
                                dataReference = i;
                            }

                            if (newSample) {
                                newSample = false;
                                data.setName(mask.getName());
                                dataList.add(data);
                            }
                        }
                    }
                }
            }

            newSample = true;
        }

        // Determine the averges of the data and find the diffs
        double[][] diffs = new double[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            DeconvolutedData data = dataList.get(i);
            data.average();

            double[] values = data.getAveragedGFP();
            double[] times = data.getAveragedTimes();
            diffs[i] = new double[values.length - 1];
            for (int j = 0; j < values.length - 1; j++) {
                diffs[i][j] = (values[j + 1] + values[j]) / (times[j + 1] / times[j]);
            }
        }

        runtime.getEventThread().postEvent(new DeconvolutionCompleteEvent());

        // Identify cross-sample average fluorescence production rates
        int times = diffs[0].length;
        double[] ratePoints = new double[times];
        for (int i = 0; i < times; i++) {
            double average = 0;
            for (int j = 0; j < diffs.length; j++) {
                average += diffs[j][i];
            }

            average /= diffs.length;
            ratePoints[i] = average;
        }

        // Identify optimum fluorescence production time
        int optimumMin = 0;
        for (int i = 0; i < ratePoints.length; i++) {
            if (optimumMin < ratePoints[i]) {
                optimumMin = i;
            }
        }

        runtime.getEventThread().postEvent(new RateCalculationCompleteEvent());

        // Calculate the reference standard's rate of fluorescence production
        double rateStandard = 0;
        for (DeconvolutedData data : dataList) {
            if (data.getSampleIdentifier() == dataReference) {
                rateStandard = (data.getAveragedGFP()[optimumMin + 1] + data.getAveragedGFP()[optimumMin]) / (data.getAveragedTimes()[optimumMin + 1] + data.getAveragedTimes()[optimumMin]);
                break;
            }
        }

        // Calculate rates for all the samples
        double[] maxRateOfProduction = new double[diffs.length];
        for (int i = 0; i < diffs.length; i++) {
            DeconvolutedData data = dataList.get(i);
            maxRateOfProduction[i] = (data.getAveragedGFP()[optimumMin + 1] + data.getAveragedGFP()[optimumMin]) / (data.getAveragedTimes()[optimumMin + 1] + data.getAveragedTimes()[optimumMin]);
        }

        // Calculate the relative strengths for the samples
        double[] relativeStrengths = new double[maxRateOfProduction.length];
        for (int i = 0; i < maxRateOfProduction.length; i++) {
            relativeStrengths[i] = maxRateOfProduction[i] / rateStandard;
        }

        // Append information
        for (DeconvolutedData data : dataList) {
            data.setRelativeStrength(relativeStrengths[data.getSampleIdentifier()]);
        }

        runtime.getEventThread().postEvent(new CharacterisationCompleteEvent(dataList));
    }

    @Override
    public void killTask(IPluginRuntime runtime) {

    }
}
