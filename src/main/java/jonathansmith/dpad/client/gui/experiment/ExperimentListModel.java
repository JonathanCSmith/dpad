package jonathansmith.dpad.client.gui.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.database.ExperimentRecord;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * List model for experiments
 */
public class ExperimentListModel extends AbstractListModel {

    public List<ExperimentRecord> experimentRecordList = new ArrayList<ExperimentRecord>();

    @Override
    public int getSize() {
        return this.experimentRecordList.size();
    }

    @Override
    public Object getElementAt(int index) {
        return this.experimentRecordList.get(index).getExperimentName();
    }

    public void addElement(ExperimentRecord record) {
        this.experimentRecordList.add(record);
        this.fireIntervalAdded(this, this.experimentRecordList.size() - 1, this.experimentRecordList.size() - 1);
    }

    public ExperimentRecord getRecord(int index) {
        return this.experimentRecordList.get(index);
    }

    public void setRecords(HashSet<ExperimentRecord> list) {
        this.experimentRecordList.clear();

        for (ExperimentRecord record : list) {
            this.addElement(record);
        }
    }

    public void clearRecords() {
        this.experimentRecordList.clear();
    }
}
