package jonathansmith.dpad.api.database;

import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Jon on 29/09/2014.
 */
@Entity
@Table(name = "Dataset")
public class DatasetRecord extends Record {

    private SampleRecord               sampleRecord;
    private MeasurementConditionRecord measurementType;
    private LoadingPluginRecord        pluginSource;
    private int[][]                    data;

    @ManyToOne(fetch = EAGER)
    public SampleRecord getSampleRecord() {
        return sampleRecord;
    }

    public void setSampleRecord(SampleRecord sampleRecord) {
        this.sampleRecord = sampleRecord;
    }

    @ManyToOne(fetch = EAGER)
    public MeasurementConditionRecord getMeasurementType() {
        return this.measurementType;
    }

    public void setMeasurementType(MeasurementConditionRecord measurementType) {
        this.measurementType = measurementType;
    }

    @ManyToOne(fetch = EAGER)
    public LoadingPluginRecord getPluginSource() {
        return pluginSource;
    }

    public void setPluginSource(LoadingPluginRecord pluginSource) {
        this.pluginSource = pluginSource;
    }

    @Column(name = "Data")
    public int[][] getMeasurements() {
        return this.data;
    }

    public void setMeasurements(int[][] measurements) {
        this.data = measurements;
    }

    public void addMeasurement(int index, int[] ints) {
        this.data[index] = ints;
    }

    public void setDataSize(int size) {
        this.data = new int[size][];
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DatasetRecord) {
            DatasetRecord r = (DatasetRecord) o;
            if (r.getSampleRecord().equals(this.getSampleRecord())
                    && r.getMeasurementType().equals(this.getMeasurementType())
                    && r.getPluginSource().equals(this.getPluginSource())) {
                int[][] rData = r.getMeasurements();
                if (rData.length == this.getMeasurements().length) {
                    for (int i = 0; i < rData.length; i++) {
                        if (rData[i].length == this.getMeasurements()[i].length) {
                            for (int j = 0; j < rData[i].length; j++) {
                                if (rData[i][j] != this.getMeasurements()[i][j]) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }
}
