package jonathansmith.microplatetxtloader.display.firststage;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/**
 * Created by Jon on 29/09/2014.
 */
public class TimesTableModel extends AbstractTableModel {

    private LinkedList<File>    fileList = new LinkedList<File>();
    private LinkedList<Integer> times    = new LinkedList<Integer>();

    public TimesTableModel() {
    }

    @Override
    public int getRowCount() {
        return this.fileList.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "File Path:";

            case 1:
                return "Associated Time:";

            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnIndex == 0 ? this.fileList.get(rowIndex).getName() : this.times.get(rowIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            this.times.set(rowIndex, Integer.parseInt((String) aValue));
            this.fireTableDataChanged();
        }
    }

    public void setFiles(LinkedList<File> file_list) {
        this.fileList = file_list;
        this.times = new LinkedList<Integer>(Collections.nCopies(this.fileList.size(), -1));
        this.fireTableDataChanged();
        this.fireTableRowsInserted(0, this.fileList.size() - 1);
    }

    public boolean containsInvalidTimes() {
        Set<Integer> set = new HashSet<Integer>(this.times);
        return set.size() < this.times.size() || this.times.contains(-1);

    }

    public LinkedList<Integer> getTimes() {
        return this.times;
    }
}
