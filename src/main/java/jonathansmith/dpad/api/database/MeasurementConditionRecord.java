package jonathansmith.dpad.api.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Jon on 29/09/2014.
 */
@Entity
@Table(name = "Measurement_Condition")
public class MeasurementConditionRecord extends Record {

    private String measurementType;

    @Column(name = "Measurement_Type")
    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MeasurementConditionRecord) {
            if (((MeasurementConditionRecord) o).getMeasurementType().contentEquals(this.getMeasurementType())) {
                return true;
            }
        }

        return false;
    }
}
