package jonathansmith.kellycharacterisationanalysis.data;

/**
 * Created by Jon on 29/09/2014.
 */
public class KellyDeconvolutionMask {

    private String  name;
    private String  measurementDetails;
    private String  pluginSource;
    private int     sampleNumber;
    private int     independentRepeatNumber;
    private int     dependentRepeatNumber;
    private boolean isReferenceSample;
    private int[][] data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasurementDetails() {
        return measurementDetails;
    }

    public void setMeasurementDetails(String measurementDetails) {
        this.measurementDetails = measurementDetails;
    }

    public String getPluginSource() {
        return pluginSource;
    }

    public void setPluginSource(String pluginSource) {
        this.pluginSource = pluginSource;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public int getIndependentRepeatNumber() {
        return independentRepeatNumber;
    }

    public void setIndependentRepeatNumber(int independentRepeatNumber) {
        this.independentRepeatNumber = independentRepeatNumber;
    }

    public int getDependentRepeatNumber() {
        return dependentRepeatNumber;
    }

    public void setDependentRepeatNumber(int dependentRepeatNumber) {
        this.dependentRepeatNumber = dependentRepeatNumber;
    }

    public boolean isReferenceSample() {
        return isReferenceSample;
    }

    public void setReferenceSample(boolean isReferenceSample) {
        this.isReferenceSample = isReferenceSample;
    }

    public int[][] getData() {
        return data;
    }

    public void setData(int[][] data) {
        this.data = data;
    }
}
