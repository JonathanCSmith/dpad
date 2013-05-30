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
package net.jonathansmith.javadpad.gui.client.batch.panel;

/**
 *
 * @author jonathansmith
 */
public class NewBatchPane extends javax.swing.JPanel {

    /**
     * Creates new form NewExperimentPane
     */
    public NewBatchPane() {
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
        jLabel2 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        submit = new javax.swing.JButton();

        jLabel1.setText("New Batch:");

        jLabel2.setText("Name:");

        jLabel3.setText("Description:");

        description.setColumns(20);
        description.setLineWrap(true);
        description.setRows(5);
        description.setWrapStyleWord(true);
        jScrollPane1.setViewportView(description);

        submit.setText("Submit");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(layout.createSequentialGroup()
                            .add(5, 5, 5)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabel1)
                                .add(layout.createSequentialGroup()
                                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(name))))
                        .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(jLabel3)
                            .add(5, 5, 5)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 380, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(submit)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jLabel1)
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(name, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(submit)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {jLabel2, name}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextArea description;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextField name;
    public javax.swing.JButton submit;
    // End of variables declaration//GEN-END:variables
}