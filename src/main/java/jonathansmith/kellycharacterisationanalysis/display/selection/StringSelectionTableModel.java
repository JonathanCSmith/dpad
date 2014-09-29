package jonathansmith.kellycharacterisationanalysis.display.selection;

import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

/**
 * Created by Jon on 29/09/2014.
 */
public class StringSelectionTableModel extends AbstractTableModel {

    private LinkedList<String> names = new LinkedList<String>();
    private boolean[] selections;

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Record:";

            case 1:
                return "Selected:";

            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;

            case 1:
                return boolean.class;

            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public int getRowCount() {
        return this.names.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnIndex == 0 ? this.names.get(rowIndex) : this.selections[rowIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            this.selections[rowIndex] = (Boolean) aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public void setData(LinkedList<String> values) {
        this.names = values;
        this.selections = new boolean[this.names.size()];
        this.fireTableDataChanged();
        this.fireTableRowsInserted(0, this.names.size());
    }

    public LinkedList<String> getSelectedNames() {
        LinkedList<String> selectedNames = new LinkedList<String>();
        for (int i = 0; i < this.names.size(); i++) {
            if (this.selections[i]) {
                selectedNames.add(this.names.get(i));
            }
        }

        return selectedNames;
    }
}
