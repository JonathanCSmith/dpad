/*
 * Copyright (C) 2013 Jonathan Smith
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
package net.jonathansmith.javadpad.gui;


import java.util.Observable;
import java.util.Observer;

import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.gui.clientmain.ClientMainPane;
import net.jonathansmith.javadpad.gui.clientmain.ClientMainToolbar;
import net.jonathansmith.javadpad.gui.startup.StartupPane;
import net.jonathansmith.javadpad.gui.startup.StartupToolbar;
import net.jonathansmith.javadpad.gui.user.panel.NewUserPane;
import net.jonathansmith.javadpad.gui.user.UserSelect;
import net.jonathansmith.javadpad.gui.user.toolbar.UserToolbar;
import net.jonathansmith.javadpad.util.RuntimeType;
import static net.jonathansmith.javadpad.util.RuntimeType.IDLE_LOCAL;
import net.jonathansmith.javadpad.util.logging.LogHandler;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author Jonathan Smith
 */
public class DPADGui extends JFrame implements Runnable, Observer {

    public DPADEngine engine;
    public DPADController controller;
    public RuntimeType type;
    public boolean errored = false;
    
    public StartupPane startupPane;
    public StartupToolbar startupToolbar;
    
    public ClientMainPane clientMainPane;
    public ClientMainToolbar clientMainToolbar;
    
    public UserSelect userSelect;
    
    /**
     * Creates new form DPADG
     */
    public DPADGui(DPADController controller) {
        this.controller = controller;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerSplitPane = new javax.swing.JSplitPane();
        headerPanel = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        toolbarSplitPane = new javax.swing.JSplitPane();
        displaySplitPane = new javax.swing.JSplitPane();
        textScroll = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        headerSplitPane.setDividerLocation(70);
        headerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        headerPanel.setMaximumSize(new java.awt.Dimension(32767, 70));
        headerPanel.setMinimumSize(new java.awt.Dimension(0, 70));

        title.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        title.setText("Data Processing Analysis and Display :");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 394, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 53, Short.MAX_VALUE))
        );

        headerSplitPane.setTopComponent(headerPanel);

        toolbarSplitPane.setDividerLocation(115);

        displaySplitPane.setDividerLocation(240);
        displaySplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        displaySplitPane.setResizeWeight(1.0);
        displaySplitPane.setAutoscrolls(true);
        displaySplitPane.setPreferredSize(new java.awt.Dimension(522, 228));

        textScroll.setMinimumSize(new java.awt.Dimension(120, 23));
        textScroll.setPreferredSize(new java.awt.Dimension(120, 22));

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText("DPAD Console Log:");
        textArea.setMinimumSize(new java.awt.Dimension(140, 120));
        textArea.setPreferredSize(new java.awt.Dimension(164, 120));
        textScroll.setViewportView(textArea);

        displaySplitPane.setRightComponent(textScroll);

        toolbarSplitPane.setRightComponent(displaySplitPane);

        headerSplitPane.setRightComponent(toolbarSplitPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerSplitPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    public void init() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
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
        
        this.startupPane = new StartupPane();
        this.startupToolbar = new StartupToolbar();
        
        this.clientMainPane = new ClientMainPane();
        this.clientMainToolbar = new ClientMainToolbar();
        
        this.userSelect = new UserSelect();
        
        DPADLogger.addLogHandler(new LogHandler(this));
    }
    
    @Override
    public void run() {
        this.setVisible(true);
        this.type = RuntimeType.RUNTIME_SELECT;
        this.validateState();
    }
    
    public void setEngine(DPADEngine engine) {
        if (this.engine != null) {
            DPADLogger.warning("Cannot change the DPAD engine once it has been set");
            return;
        }
        
        this.engine = engine;
        this.engine.addObserver(this);
    }
    
    @Override
    public void update(Observable obs, Object obj) {
        if (obs == this.engine) {
            RuntimeType currentRuntime = this.engine.getCurrentRuntime();
            if (currentRuntime != this.type && currentRuntime.isDisplayable()) {
                this.type = this.engine.getCurrentRuntime();
                this.validateState();
            }
        }
    }
    
    public void updateLog(String message) {
        this.textArea.append(message);
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        this.getContentPane().validate();
    }
    
    public String getDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int outcome = chooser.showOpenDialog(this);
        
        String output;
        if (outcome == JFileChooser.APPROVE_OPTION) {
            output = chooser.getSelectedFile().getAbsolutePath();
            
        } else {
            output = "";
        }
        
        return output;
    }
    
    public void validateState() {
        switch (this.type) {
            case RUNTIME_SELECT:        
                                    this.setCorePanels(this.startupPane, this.startupToolbar);
                                    break;
            
            case IDLE_LOCAL:        
                                    this.setCorePanels(this.clientMainPane, this.clientMainToolbar);
                                    if (this.controller.getSessionUser() == null) {
                                        this.clientMainToolbar.setExperiment.setEnabled(false);
                                        this.clientMainToolbar.setBatch.setEnabled(false);
                                        
                                    } else {
                                        this.clientMainToolbar.setExperiment.setEnabled(true);
                                        
                                        if (this.controller.getSessionExperiment() == null) {
                                            this.clientMainToolbar.setBatch.setEnabled(false);
                                            
                                        } else {
                                            this.clientMainToolbar.setBatch.setEnabled(true);
                                        }
                                    }

                                    break;
            
            case USER_SELECT:       
                                    this.setCorePanels(this.userSelect.getCurrentView(), this.userSelect.userToolbar);
                                    break;
                
            default:                break;
        }
        
        this.maintainMinimumDividerSizes();
    }
    
    private void setCorePanels(JPanel panel, JPanel toolbar) {
        this.displaySplitPane.setLeftComponent(panel);
        this.toolbarSplitPane.setLeftComponent(toolbar);
    }
    
    private void maintainMinimumDividerSizes() {
        if (this.headerSplitPane.getDividerLocation() < 70) {
            this.headerSplitPane.setDividerLocation(70);
        }
        
        if (this.toolbarSplitPane.getDividerLocation() <= 115) {
            this.toolbarSplitPane.setDividerLocation(115);
        }
        
        if (this.displaySplitPane.getDividerLocation() <= (this.displaySplitPane.getSize().height - 120)) {
            this.displaySplitPane.setDividerLocation(this.displaySplitPane.getSize().height - 120);
        }
    }
    
    public void addStartupSelectListener(ActionListener listener) {
        this.startupPane.localRuntime.addActionListener(listener);
        this.startupPane.hostRuntime.addActionListener(listener);
        this.startupPane.connectRuntime.addActionListener(listener);
    }
    
    public void addMainMenuListener(ActionListener listener) {
        this.clientMainToolbar.setUser.addActionListener(listener);
    }
    
    public void addUserRuntimeListener(ActionListener listener) {
        this.userSelect.userToolbar.newUser.addActionListener(listener);
        this.userSelect.userToolbar.loadUser.addActionListener(listener);
        this.userSelect.userToolbar.userBack.addActionListener(listener);
        this.userSelect.newUserPane.submit.addActionListener(listener);
        this.userSelect.existingUserPane.submit.addActionListener(listener);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JSplitPane displaySplitPane;
    public javax.swing.JPanel headerPanel;
    public javax.swing.JSplitPane headerSplitPane;
    public javax.swing.JTextArea textArea;
    public javax.swing.JScrollPane textScroll;
    public javax.swing.JLabel title;
    public javax.swing.JSplitPane toolbarSplitPane;
    // End of variables declaration//GEN-END:variables
}
