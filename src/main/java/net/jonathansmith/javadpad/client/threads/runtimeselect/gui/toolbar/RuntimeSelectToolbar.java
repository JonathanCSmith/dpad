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
package net.jonathansmith.javadpad.client.threads.runtimeselect.gui.toolbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

/**
 *
 * @author Jon
 */
public class RuntimeSelectToolbar extends JPanel {

    /**
     * Creates new form ClientMainToolbar
     */
    public RuntimeSelectToolbar() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setUser = new JButton();
        setExperiment = new JButton();
        jLabel4 = new JLabel();
        jLabel6 = new JLabel();
        setBatch = new JButton();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        addData = new JButton();

        setMaximumSize(new Dimension(120, 32767));
        setMinimumSize(new Dimension(120, 0));
        setPreferredSize(new Dimension(120, 300));

        setUser.setText("Set User");
        setUser.setToolTipText("Set the current user, either by creating or loading one in the datase");
        setUser.setBorder(null);
        setUser.setMargin(new Insets(2, 8, 2, 8));
        setUser.setMaximumSize(new Dimension(105, 23));
        setUser.setMinimumSize(new Dimension(105, 23));
        setUser.setPreferredSize(new Dimension(105, 23));

        setExperiment.setText("Set Experiment");
        setExperiment.setToolTipText("Create a new experiment");
        setExperiment.setBorder(null);
        setExperiment.setMargin(new Insets(2, 8, 2, 8));
        setExperiment.setMaximumSize(new Dimension(105, 23));
        setExperiment.setMinimumSize(new Dimension(105, 23));
        setExperiment.setPreferredSize(new Dimension(105, 23));

        jLabel4.setText("Experiment:");

        jLabel6.setText("Batch");

        setBatch.setText("Set Batch");

        jLabel7.setText("User");

        jLabel8.setText("Batch Data");

        addData.setText("Add Data");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(setUser, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                            .addComponent(setExperiment, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                        .addComponent(jLabel4))
                    .addComponent(setBatch, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(addData, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {setBatch, setExperiment, setUser});

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel7)
                .addGap(5, 5, 5)
                .addComponent(setUser, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addComponent(setExperiment, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(setBatch)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addData)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {setBatch, setExperiment, setUser});

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JButton addData;
    private JLabel jLabel4;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    public JButton setBatch;
    public JButton setExperiment;
    public JButton setUser;
    // End of variables declaration//GEN-END:variables
}
