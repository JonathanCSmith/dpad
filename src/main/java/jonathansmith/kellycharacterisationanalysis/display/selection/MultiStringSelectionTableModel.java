package jonathansmith.kellycharacterisationanalysis.display.selection;

import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

/**
 * Created by Jon on 29/09/2014.
 */
public class MultiStringSelectionTableModel extends AbstractTableModel {

    private LinkedList<String> samples      = new LinkedList<String>();
    private LinkedList<String> measurements = new LinkedList<String>();
    private LinkedList<String> plugins      = new LinkedList<String>();
    private boolean[] selections;

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Sample Name:";

            case 1:
                return "Measurement Info:";

            case 2:
                return "Plugin Name:";

            case 3:
                return "Selected:";

            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 3 ? boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3;
    }

    @Override
    public int getRowCount() {
        return this.samples.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return this.samples.get(rowIndex);

            case 1:
                return this.measurements.get(rowIndex);

            case 2:
                return this.plugins.get(rowIndex);

            case 3:
                return this.selections[rowIndex];
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            this.selections[rowIndex] = (Boolean) aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public void setData(LinkedList<String> samples, LinkedList<String> measurements, LinkedList<String> plugins) {
        this.samples = samples;
        this.measurements = measurements;
        this.plugins = plugins;
        this.selections = new boolean[this.samples.size()];
        this.fireTableDataChanged();
        this.fireTableRowsInserted(0, this.samples.size());
    }

    public LinkedList<String> getSelectedSamples() {
        LinkedList<String> selectedSamples = new LinkedList<String>();
        for (int i = 0; i < this.samples.size(); i++) {
            if (this.selections[i]) {
                selectedSamples.add(this.samples.get(i));
            }
        }

        return selectedSamples;
    }
}
