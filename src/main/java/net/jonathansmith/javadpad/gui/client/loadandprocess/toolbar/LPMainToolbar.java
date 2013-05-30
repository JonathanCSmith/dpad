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
package net.jonathansmith.javadpad.gui.client.loadandprocess.toolbar;

/**
 *
 * @author Jon
 */
public class LPMainToolbar extends javax.swing.JPanel {

    /**
     * Creates new form ClientMainToolbar
     */
    public LPMainToolbar() {
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

        setUser = new javax.swing.JButton();
        setExperiment = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        setBatch = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        addData = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(120, 32767));
        setMinimumSize(new java.awt.Dimension(120, 0));
        setPreferredSize(new java.awt.Dimension(120, 300));

        setUser.setText("Set User");
        setUser.setToolTipText("Set the current user, either by creating or loading one in the datase");
        setUser.setBorder(null);
        setUser.setMargin(new java.awt.Insets(2, 8, 2, 8));
        setUser.setMaximumSize(new java.awt.Dimension(105, 23));
        setUser.setMinimumSize(new java.awt.Dimension(105, 23));
        setUser.setPreferredSize(new java.awt.Dimension(105, 23));

        setExperiment.setText("Set Experiment");
        setExperiment.setToolTipText("Create a new experiment");
        setExperiment.setBorder(null);
        setExperiment.setMargin(new java.awt.Insets(2, 8, 2, 8));
        setExperiment.setMaximumSize(new java.awt.Dimension(105, 23));
        setExperiment.setMinimumSize(new java.awt.Dimension(105, 23));
        setExperiment.setPreferredSize(new java.awt.Dimension(105, 23));

        jLabel4.setText("Experiment:");

        jLabel6.setText("Batch");

        setBatch.setText("Set Batch");

        jLabel7.setText("User");

        jLabel8.setText("Batch Data");

        addData.setText("Add Data");
        addData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(setUser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                            .addComponent(setExperiment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                        .addComponent(jLabel4))
                    .addComponent(setBatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(addData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {setBatch, setExperiment, setUser});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel7)
                .addGap(5, 5, 5)
                .addComponent(setUser, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addComponent(setExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(setBatch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addData)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {setBatch, setExperiment, setUser});

    }// </editor-fold>//GEN-END:initComponents

    private void addDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addDataActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton addData;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    public javax.swing.JButton setBatch;
    public javax.swing.JButton setExperiment;
    public javax.swing.JButton setUser;
    // End of variables declaration//GEN-END:variables
}
