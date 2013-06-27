/*
 * Copyright (C) 2013 Jon
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
package net.jonathansmith.javadpad.client.threads.data.pane;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.jonathansmith.javadpad.client.gui.displayoptions.pane.CurrentRecordPane;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.records.LoaderDataSet;
import net.jonathansmith.javadpad.common.database.records.LoaderPluginRecord;

/**
 *
 * @author Jon
 */
public class AddDataPane extends CurrentRecordPane {

    /**
     * Creates new form ClientMainPane
     */
    public AddDataPane() {
        initComponents();
    }
    
    @Override
    public void setCurrentData(Record record) {
        if (record instanceof LoaderDataSet) {
            LoaderDataSet l = (LoaderDataSet) record;
            this.pluginName.setText(l.getPluginInfo().getName());
            this.equipmentName.setText(((LoaderPluginRecord) l.getPluginInfo()).getEquipment().getName());
            DefaultListModel list = (DefaultListModel) this.jList1.getModel();
            list.removeAllElements();
            
            for (String file : l.getSourceFiles()) {
                list.addElement(file);
            }
            
            this.numberOfObserved.setText(l.getData().size() + " samples in this dataset");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        pluginName = new JTextField();
        experimentDescription1 = new JLabel();
        numberOfObserved = new JTextField();
        equipmentName = new JTextField();
        jLabel3 = new JLabel();
        jScrollPane2 = new JScrollPane();
        jList1 = new JList();

        jLabel1.setText("Plugin Name:");

        jLabel2.setText("Equipment Name:");

        pluginName.setEditable(false);
        pluginName.setBackground(new Color(255, 255, 255));

        experimentDescription1.setText("Samples:");

        numberOfObserved.setEditable(false);
        numberOfObserved.setBackground(new Color(255, 255, 255));

        equipmentName.setEditable(false);
        equipmentName.setBackground(new Color(255, 255, 255));
        equipmentName.setToolTipText("");

        jLabel3.setText("Files:");

        jList1.setModel(new DefaultListModel());
        jScrollPane2.setViewportView(jList1);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(experimentDescription1)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                        .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(numberOfObserved, GroupLayout.PREFERRED_SIZE, 283, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 283, GroupLayout.PREFERRED_SIZE)
                    .addComponent(equipmentName, GroupLayout.PREFERRED_SIZE, 283, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pluginName, GroupLayout.PREFERRED_SIZE, 283, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {experimentDescription1, jLabel1, jLabel2});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {equipmentName, jScrollPane2, numberOfObserved, pluginName});

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pluginName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(equipmentName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(numberOfObserved, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(experimentDescription1))
                .addGap(48, 48, 48))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextField equipmentName;
    private JLabel experimentDescription1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JList jList1;
    private JScrollPane jScrollPane2;
    private JTextField numberOfObserved;
    private JTextField pluginName;
    // End of variables declaration//GEN-END:variables
}