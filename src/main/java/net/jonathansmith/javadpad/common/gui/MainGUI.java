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
package net.jonathansmith.javadpad.common.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import net.jonathansmith.javadpad.DPAD;
import net.jonathansmith.javadpad.common.events.EventListener;
import net.jonathansmith.javadpad.common.events.gui.ContentChangedEvent;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 *
 * @author jonathansmith
 */
public class MainGUI extends JFrame implements Runnable, EventListener {

    private final DPAD main;
    
    /**
     * Creates new form StartupGUI
     */
    public MainGUI(DPAD main) {
        this.main = main;
    }
    
    public void init() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
        } catch (InstantiationException ex) {
            System.out.println("Look and feel instantiation exception");
        } catch (IllegalAccessException ex) {
            System.out.println("Illegal access of look and feel");
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println("Unsupported look and feel");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found when setting look and feel");
        } 
        
        this.initComponents();
    }
    
    public void run() {
        this.setVisible(true);
    }
    
    public void disposeOfStartupPane() {
        this.remove(this.startupPanel);
        this.repaint();
    }
    
    public void addTab(String tabName, TabbedGUI tabContents) {
        this.mainDisplay.addTab(tabName, tabContents);
    }
    
    public void removeTab(TabbedGUI tab) {
        this.mainDisplay.remove(tab);
        
        if (this.mainDisplay.getTabCount() == 0) {
            this.dispose();
        }
    }
    
    @Override
    public void changeEventReceived(EventObject event) {
        if (event instanceof ContentChangedEvent) {
            this.revalidate();
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

        mainDisplay = new JTabbedPane();
        startupPanel = new JPanel();
        databaseClientURL = new JTextField();
        connectRuntime = new JButton();
        hostRuntime = new JButton();
        localInfo = new JLabel();
        localRuntime = new JButton();
        jLabel8 = new JLabel();
        databaseHostURL = new JTextField();
        jTextArea1 = new JTextArea();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent evt) {
                GuiCloseListener(evt);
            }
        });

        mainDisplay.setPreferredSize(new Dimension(502, 296));

        databaseClientURL.setText("Enter database address!");

        connectRuntime.setText("Connect");

        hostRuntime.setText("Host");

        localInfo.setText("Create or load a local database (jar location)");

        localRuntime.setText("Local");

        jLabel8.setText("Database Connection:");

        databaseHostURL.setText("Enter database port!");

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Enter the URL of your database in the box above. If you do not have a configured database enter the path of a local database that you would like to use. This path will be used to search for viable databases or create a new one.");
        jTextArea1.setToolTipText("");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setCaretColor(new Color(240, 240, 240));

        GroupLayout startupPanelLayout = new GroupLayout(startupPanel);
        startupPanel.setLayout(startupPanelLayout);
        startupPanelLayout.setHorizontalGroup(
            startupPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 502, Short.MAX_VALUE)
            .add(startupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                .add(startupPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(startupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                        .add(startupPanelLayout.createSequentialGroup()
                            .add(jLabel8)
                            .add(375, 375, 375))
                        .add(startupPanelLayout.createSequentialGroup()
                            .add(startupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                                .add(startupPanelLayout.createSequentialGroup()
                                    .add(10, 10, 10)
                                    .add(startupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                                        .add(localInfo)
                                        .add(databaseClientURL, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE))
                                    .add(5, 5, 5))
                                .add(GroupLayout.TRAILING, startupPanelLayout.createSequentialGroup()
                                    .add(databaseHostURL, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.RELATED)))
                            .add(startupPanelLayout.createParallelGroup(GroupLayout.LEADING, false)
                                .add(GroupLayout.TRAILING, localRuntime, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                .add(GroupLayout.TRAILING, connectRuntime, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                .add(GroupLayout.TRAILING, hostRuntime, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(0, 0, Short.MAX_VALUE))
                        .add(startupPanelLayout.createSequentialGroup()
                            .add(10, 10, 10)
                            .add(jTextArea1)))
                    .addContainerGap()))
        );

        startupPanelLayout.linkSize(new Component[] {connectRuntime, hostRuntime, localRuntime}, GroupLayout.HORIZONTAL);

        startupPanelLayout.setVerticalGroup(
            startupPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 296, Short.MAX_VALUE)
            .add(startupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                .add(startupPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(jLabel8)
                    .add(10, 10, 10)
                    .add(startupPanelLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(localRuntime)
                        .add(localInfo))
                    .add(4, 4, 4)
                    .add(startupPanelLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(hostRuntime)
                        .add(databaseHostURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .add(5, 5, 5)
                    .add(startupPanelLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(databaseClientURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .add(connectRuntime))
                    .add(10, 10, 10)
                    .add(jTextArea1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(79, Short.MAX_VALUE)))
        );

        startupPanelLayout.linkSize(new Component[] {connectRuntime, hostRuntime, localRuntime}, GroupLayout.VERTICAL);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(startupPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
            .add(layout.createParallelGroup(GroupLayout.LEADING)
                .add(mainDisplay, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(startupPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createParallelGroup(GroupLayout.LEADING)
                .add(mainDisplay, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GuiCloseListener(WindowEvent evt) {//GEN-FIRST:event_GuiCloseListener
        this.setVisible(false);
        this.dispose();
        this.main.shutdown();
    }//GEN-LAST:event_GuiCloseListener

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JButton connectRuntime;
    public JTextField databaseClientURL;
    public JTextField databaseHostURL;
    public JButton hostRuntime;
    private JLabel jLabel8;
    private JTextArea jTextArea1;
    private JLabel localInfo;
    public JButton localRuntime;
    private JTabbedPane mainDisplay;
    private JPanel startupPanel;
    // End of variables declaration//GEN-END:variables
}
