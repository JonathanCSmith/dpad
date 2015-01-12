package jonathansmith.dpad.api.plugins.data;

import java.util.TreeMap;

import jonathansmith.dpad.api.plugins.records.IPluginRecord;

/**
 * Created by Jon on 29/09/2014.
 */
public class Dataset {

    private String sampleName;
    private String sampleMeasurementCondition;
    private TreeMap<Integer, Integer> data = new TreeMap<Integer, Integer>();
    private IPluginRecord pluginSource;

    public String getSampleName() {
        return this.sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleMeasurementCondition() {
        return this.sampleMeasurementCondition;
    }

    public void setSampleMeasurementCondition(String sampleMeasurementCondition) {
        this.sampleMeasurementCondition = sampleMeasurementCondition;
    }

    public TreeMap<Integer, Integer> getMeasurements() {
        return this.data;
    }

    public void addMeasurement(Integer key, int measurementData) {
        this.data.put(key, measurementData);
    }

    public IPluginRecord getPluginSource() {
        return this.pluginSource;
    }

    public void setPluginSource(IPluginRecord pluginSource) {
        this.pluginSource = pluginSource;
    }

    public void setData(TreeMap<Integer, Integer> data) {
        this.data = data;
    }
}
