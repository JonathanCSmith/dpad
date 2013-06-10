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
package net.jonathansmith.javadpad.aaaarewrite.common.startup;

import net.jonathansmith.javadpad.util.logging.DPADLogger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author jonathansmith
 */
public class StartupGUI extends javax.swing.JFrame implements Runnable {

    /**
     * Creates new form StartupGUI
     */
    public StartupGUI() {}
    
    public void init() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
        } catch (InstantiationException ex) {
            DPADLogger.severe("Look and feel instantiation exception");
        } catch (IllegalAccessException ex) {
            DPADLogger.severe("Illegal access of look and feel");
        } catch (UnsupportedLookAndFeelException ex) {
            DPADLogger.severe("Unsupported look and feel");
        } catch (ClassNotFoundException ex) {
            DPADLogger.severe("Class not found when setting look and feel");
        } 
        
        this.initComponents();
    }
    
    public void run() {
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        databaseClientURL = new javax.swing.JTextField();
        connectRuntime = new javax.swing.JButton();
        hostRuntime = new javax.swing.JButton();
        localInfo = new javax.swing.JLabel();
        localRuntime = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        databaseHostURL = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setBorder(null);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Enter the URL of your database in the box above. If you do not have a configured database enter the path of a local database that you would like to use. This path will be used to search for viable databases or create a new one.");
        jTextArea1.setToolTipText("");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setCaretColor(new java.awt.Color(240, 240, 240));
        jScrollPane1.setViewportView(jTextArea1);

        databaseClientURL.setText("Enter database address!");

        connectRuntime.setText("Connect");

        hostRuntime.setText("Host");

        localInfo.setText("Create or load a local database (jar location)");

        localRuntime.setText("Local");

        jLabel8.setText("Database Connection:");

        databaseHostURL.setText("Enter database port!");
        databaseHostURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseHostURLActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel8)
                .add(0, 0, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jScrollPane1))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(localInfo)
                                    .add(databaseClientURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 280, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(5, 5, 5))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .add(databaseHostURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 280, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, localRuntime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, connectRuntime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, hostRuntime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .add(10, 10, 10))
        );

        layout.linkSize(new java.awt.Component[] {connectRuntime, hostRuntime, localRuntime}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel8)
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(localRuntime)
                    .add(localInfo))
                .add(4, 4, 4)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hostRuntime)
                    .add(databaseHostURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(databaseClientURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(connectRuntime))
                .add(5, 5, 5)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {connectRuntime, hostRuntime, localRuntime}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void databaseHostURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseHostURLActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_databaseHostURLActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton connectRuntime;
    public javax.swing.JTextField databaseClientURL;
    public javax.swing.JTextField databaseHostURL;
    public javax.swing.JButton hostRuntime;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel localInfo;
    public javax.swing.JButton localRuntime;
    // End of variables declaration//GEN-END:variables

}