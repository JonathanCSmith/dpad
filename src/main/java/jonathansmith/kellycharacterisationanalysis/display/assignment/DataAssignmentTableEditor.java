package jonathansmith.kellycharacterisationanalysis.display.assignment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

/**
 * Created by Jon on 29/09/2014.
 */
public class DataAssignmentTableEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private final String[] samples;
    private final String[] independents;
    private final String[] dependents;

    private String value;

    public DataAssignmentTableEditor(String[] sampleList, String[] independentRepeatList, String[] dependentRepeatList) {
        this.samples = sampleList;
        this.independents = independentRepeatList;
        this.dependents = dependentRepeatList;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JComboBox<String> comboBox = (JComboBox<String>) ae.getSource();
        this.value = (String) comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        String[] list = new String[]{};
        switch (column) {
            case 0:
                return null;
            case 1:
                return null;
            case 2:
                return null;
            case 3:
                list = this.dependents;
            case 4:
                if (column == 4) {
                    list = this.dependents;
                }
            case 5:
                if (column == 5) {
                    list = this.dependents;
                }

                JComboBox<String> comboBox = new JComboBox<String>();
                for (String val : list) {
                    comboBox.addItem(val);
                }
                comboBox.setSelectedItem(value);
                comboBox.addActionListener(this);

                if (isSelected) {
                    comboBox.setBackground(table.getSelectionBackground());
                }

                else {
                    comboBox.setBackground(table.getForeground());
                }

                return comboBox;
            case 6:
                return null;
            default:
                return null;
        }
    }

    @Override
    public Object getCellEditorValue() {
        return this.value;
    }
}
