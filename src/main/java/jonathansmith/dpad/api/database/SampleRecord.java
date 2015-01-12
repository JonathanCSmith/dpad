package jonathansmith.dpad.api.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Jon on 29/09/2014.
 */
@Entity
@Table(name = "Sample")
public class SampleRecord extends Record {

    private String sampleName;

    @Column(name = "Sample_Name")
    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SampleRecord && ((SampleRecord) o).getSampleName().contentEquals(this.getSampleName());
    }
}
