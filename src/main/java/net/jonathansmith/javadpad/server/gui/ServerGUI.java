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
package net.jonathansmith.javadpad.server.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.DPADEvent;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;
import net.jonathansmith.javadpad.server.Server;

/**
 *
 * @author Jon
 */
public class ServerGUI extends TabbedGUI {

    public final Server engine;
    
    private final CopyOnWriteArrayList<EventListener> listeners;
    
    private int[] textFieldLength = new int[1024];
    private int currentTextLength = 0;
    
    /**
     * Creates new form ServerGUI
     */
    public ServerGUI(Server server) {
        this.engine = server;
        this.listeners = new CopyOnWriteArrayList<EventListener> ();
    }
    
    @Override
    public void init() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
        } catch (InstantiationException ex) {
            this.engine.error("Look and feel instantiation exception", ex);
        } catch (IllegalAccessException ex) {
            this.engine.error("Illegal access of look and feel", ex);
        } catch (UnsupportedLookAndFeelException ex) {
            this.engine.error("Unsupported look and feel", ex);
        } catch (ClassNotFoundException ex) {
            this.engine.error("Class not found when setting look and feel", ex);
        } 
        
        this.initComponents();
    }
    
    @Override
    public void run() {
        this.setVisible(true);
    }
    
    @Override
    public void updateLog(String message) {
        this.logText.append(message);
        
        int oldLength = this.logText.getDocument().getLength();
        this.logText.setCaretPosition(this.logText.getDocument().getLength());
        int addedSize = this.logText.getDocument().getLength() - oldLength;
        
        if (this.textFieldLength[this.currentTextLength] != 0) {
            this.logText.replaceRange("", 0, this.textFieldLength[this.currentTextLength]);
        }
        
        this.textFieldLength[this.currentTextLength] = addedSize;
        this.currentTextLength = (this.currentTextLength + 1) % 1024;
    }
    
    public void fireChange(DPADEvent event) {
        for (EventListener listener : this.listeners) {
            listener.changeEventReceived(event);
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

        jSplitPane1 = new JSplitPane();
        jScrollPane2 = new JScrollPane();
        logText = new JTextArea();
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jButton1 = new JButton();

        logText.setColumns(20);
        logText.setRows(5);
        jScrollPane2.setViewportView(logText);

        jSplitPane1.setRightComponent(jScrollPane2);

        jLabel1.setText("DPAD Server:");

        jButton1.setText("Shutdown");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 245, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(5, 5, 5))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.engine.saveAndShutdown();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton jButton1;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane2;
    private JSplitPane jSplitPane1;
    private JTextArea logText;
    // End of variables declaration//GEN-END:variables
}
