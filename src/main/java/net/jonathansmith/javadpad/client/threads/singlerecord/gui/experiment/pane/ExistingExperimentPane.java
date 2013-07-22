/*
 * Copyright (C) 2013 jonathansmith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonathansmith.javadpad.client.threads.singlerecord.gui.experiment.pane;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.jonathansmith.javadpad.client.threads.singlerecord.gui.pane.ExistingRecordPane;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 *
 * @author jonathansmith
 */
public class ExistingExperimentPane extends ExistingRecordPane {
    
    /**
     * Creates new form ExistingExperimentPane
     */
    public ExistingExperimentPane() {
        initComponents();
        this.jList1.getSelectionModel().addListSelectionListener(new ListSelection());
    }
    
    @Override
    public void addDisplayOptionListener(ActionListener listener) {
        this.submit.addActionListener(listener);
    }

    @Override
    public void addDisplayOptionMouseListener(MouseListener listener) {
        this.jList1.addMouseListener(listener);
    }
    
    @Override
    public boolean isEventSourceSubmitButton(ActionEvent event) {
        if (event.getSource() == this.submit) {
            return true;
        }
        
        return false;
    }

    @Override
    public void insertRecords(RecordsList<Record> data) {
        ((ExperimentListModel) this.jList1.getModel()).setData(data);
        this.jList1.repaint();
    }
    
    @Override
    public void clearRecords() {
        ((ExperimentListModel) this.jList1.getModel()).clearData();
        this.jList1.repaint();
    }

    @Override
    public Record getSelectedRecord() {
        int rowNum = this.jList1.getSelectedIndex();
        if (rowNum == -1) {
            return null;
        }
        
        return ((ExperimentListModel) this.jList1.getModel()).getData(rowNum);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        jScrollPane2 = new JScrollPane();
        jList1 = new JList();
        jScrollPane3 = new JScrollPane();
        jTextArea2 = new JTextArea();
        submit = new JButton();

        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setMaximumSize(new Dimension(50, 32767));
        jScrollPane1.setMinimumSize(new Dimension(50, 23));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Please select the row in the list that represents the experiment you wish to load (description is viewed on the right)");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setMaximumSize(new Dimension(50, 2147483647));
        jTextArea1.setMinimumSize(new Dimension(50, 16));
        jTextArea1.setPreferredSize(new Dimension(50, 80));
        jScrollPane1.setViewportView(jTextArea1);

        jList1.setModel(new ExperimentListModel());
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jList1);

        jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jTextArea2);

        submit.setText("Submit");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane2, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jScrollPane3))
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(submit)))
                .add(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(jScrollPane2, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .add(jScrollPane3, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
                .add(5, 5, 5)
                .add(submit)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JList jList1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JTextArea jTextArea1;
    private JTextArea jTextArea2;
    public JButton submit;
    // End of variables declaration//GEN-END:variables

    private class ListSelection implements ListSelectionListener {
        
        @Override
        public void valueChanged(ListSelectionEvent evt) {
                ListSelectionModel model = (ListSelectionModel) evt.getSource();
                int index = evt.getFirstIndex();
                if (model.isSelectedIndex(index)) {
                    String desc = ((ExperimentListModel) jList1.getModel()).getDescription(index);
                    jTextArea2.setText(desc);
                }
        }
        
    }
}