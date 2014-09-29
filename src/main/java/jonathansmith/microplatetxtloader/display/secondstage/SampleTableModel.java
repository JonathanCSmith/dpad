package jonathansmith.microplatetxtloader.display.secondstage;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;

/**
 * Created by Jon on 29/09/2014.
 */
public class SampleTableModel implements TableModel {

    private final MicroplateTXTLoader core;

    private String[][] sampleNames;

    public SampleTableModel(MicroplateTXTLoader core) {
        this.core = core;
        this.sampleNames = new String[this.core.getMicroplateHeight()][this.core.getMicroplateWidth()];
        for (int i = 0; i < sampleNames.length; i++) {
            for (int j = 0; j < sampleNames[i].length; j++) {
                sampleNames[i][j] = "NONE";
            }
        }
    }

    @Override
    public int getRowCount() {
        return this.core.getMicroplateHeight();
    }

    @Override
    public int getColumnCount() {
        return this.core.getMicroplateWidth();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.sampleNames[rowIndex][columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        this.sampleNames[rowIndex][columnIndex] = (String) aValue;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    public String[][] getValues() {
        return this.sampleNames;
    }
}
