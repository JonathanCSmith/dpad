package jonathansmith.dpad.api.database;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Experiment records for the database
 */
@Entity
@Table(name = "Experiment")
public class ExperimentRecord extends Record {

    private String experimentName = "";
    private UserRecord         user;
    private Set<DatasetRecord> datasets;

    @Column(name = "Experiment_Name")
    public String getExperimentName() {
        return this.experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    @Column(name = "User")
    public UserRecord getUser() {
        return this.user;
    }

    public void setUser(UserRecord user) {
        this.user = user;
    }

    @OneToMany(orphanRemoval = true)
    @Column(name = "Datasets")
    public Set<DatasetRecord> getDatasets() {
        return this.datasets;
    }

    public void setDatasets(Set<DatasetRecord> records) {
        this.datasets = records;
    }

    public void addDataset(DatasetRecord datasetRecord) {
        this.datasets.add(datasetRecord);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExperimentRecord) {
            ExperimentRecord experiment = (ExperimentRecord) o;
            if (experiment.getExperimentName().contentEquals(this.experimentName)
                    && experiment.getUser().equals(this.user)) {
                return true;
            }
        }

        return false;
    }
}
