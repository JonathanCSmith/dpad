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
package net.jonathansmith.javadpad.gui.experiment.toolbar;

/**
 *
 * @author jonathansmith
 */
public class ExperimentToolbar extends javax.swing.JPanel {

    /**
     * Creates new form ExperimentToolbar
     */
    public ExperimentToolbar() {
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

        jLabel1 = new javax.swing.JLabel();
        newExperiment = new javax.swing.JButton();
        loadExperiment = new javax.swing.JButton();
        experimentBack = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(120, 32767));
        setMinimumSize(new java.awt.Dimension(120, 0));

        jLabel1.setText("Experiment Panel:");

        newExperiment.setText("New");

        loadExperiment.setText("Existing");

        experimentBack.setText("Back");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .add(0, 0, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(newExperiment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(loadExperiment)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(experimentBack)))
                .addContainerGap(4, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {experimentBack, loadExperiment, newExperiment}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .add(5, 5, 5)
                .add(newExperiment)
                .add(5, 5, 5)
                .add(loadExperiment)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(experimentBack)
                .add(0, 178, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {experimentBack, loadExperiment, newExperiment}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton experimentBack;
    private javax.swing.JLabel jLabel1;
    public javax.swing.JButton loadExperiment;
    public javax.swing.JButton newExperiment;
    // End of variables declaration//GEN-END:variables
}
