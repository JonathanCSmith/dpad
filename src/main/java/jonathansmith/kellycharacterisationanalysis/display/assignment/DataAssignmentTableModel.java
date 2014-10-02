package jonathansmith.kellycharacterisationanalysis.display.assignment;

import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import jonathansmith.dpad.api.database.DatasetRecord;

import jonathansmith.kellycharacterisationanalysis.data.KellyDeconvolutionMask;

/**
 * Created by Jon on 29/09/2014.
 */
public class DataAssignmentTableModel extends AbstractTableModel {

    private LinkedList<KellyDeconvolutionMask> values = new LinkedList<KellyDeconvolutionMask>();

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Sample Name:";

            case 1:
                return "Measurement Type:";

            case 2:
                return "Plugin Source:";

            case 3:
                return "Sample Number:";

            case 4:
                return "Independent Repeat Number:";

            case 5:
                return "Dependent Repeat Number:";

            case 6:
                return "Is Reference:";

            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == 6 ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 2;
    }

    @Override
    public int getRowCount() {
        return this.values.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KellyDeconvolutionMask mask = this.values.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return mask.getName();

            case 1:
                return mask.getMeasurementDetails();

            case 2:
                return mask.getPluginSource();

            case 3:
                return String.valueOf(mask.getSampleNumber());

            case 4:
                return String.valueOf(mask.getIndependentRepeatNumber());

            case 5:
                return String.valueOf(mask.getDependentRepeatNumber());

            case 6:
                return mask.isReferenceSample();
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        KellyDeconvolutionMask mask = this.values.get(rowIndex);
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
                return;
            case 3:
                mask.setSampleNumber(Integer.parseInt((String) aValue));
                break;

            case 4:
                mask.setIndependentRepeatNumber(Integer.parseInt((String) aValue));
                break;

            case 5:
                mask.setDependentRepeatNumber(Integer.parseInt((String) aValue));
                break;

            case 6:
                mask.setReferenceSample((Boolean) aValue);
                break;
        }
    }

    public void setData(HashSet<DatasetRecord> records) {
        this.values.clear();
        for (DatasetRecord record : records) {
            KellyDeconvolutionMask mask = new KellyDeconvolutionMask();
            mask.setName(record.getSampleRecord().getSampleName());
            mask.setMeasurementDetails(record.getMeasurementType().getMeasurementType());
            mask.setPluginSource(record.getPluginSource().getPluginName());
            mask.setSampleNumber(-1);
            mask.setIndependentRepeatNumber(-1);
            mask.setDependentRepeatNumber(-1);
            mask.setReferenceSample(false);
            this.values.add(mask);
        }
    }

    public LinkedList<KellyDeconvolutionMask> getMasks() {
        return this.values;
    }
}
