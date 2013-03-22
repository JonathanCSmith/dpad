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

import java.io.File;

import java.util.Observable;
import java.util.Observer;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import net.jonathansmith.javadpad.engine.DPADLocalEngine;
import net.jonathansmith.javadpad.engine.runtime.RuntimeType;
import static net.jonathansmith.javadpad.engine.runtime.RuntimeType.IDLE;
import net.jonathansmith.javadpad.gui.handler.LogHandler;
import net.jonathansmith.javadpad.util.DPADLogger;

/**
 *
 * @author Jonathan Smith
 */
public class DPADGui extends javax.swing.JFrame implements Runnable, Observer {

    public DPADLocalEngine engine;
    public DPADLogger logger;
    public RuntimeType type;
    public boolean errored = false;
    
    /**
     * Creates new form DPADG
     */
    public DPADGui(DPADLogger logger) {
        this.logger = logger;
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
        lPToolbar = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        databaseToolbar = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        userToolbar = new javax.swing.JPanel();
        userBack = new javax.swing.JButton();
        idleToolbar = new javax.swing.JPanel();
        user = new javax.swing.JButton();
        newExperiment = new javax.swing.JButton();
        loadExperiment = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        addBatch = new javax.swing.JButton();
        displaySplitPane = new javax.swing.JSplitPane();
        textScroll = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        idlePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lPPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        userPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        databasePanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        databaseURLText = new javax.swing.JTextField();
        connectDatabase = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

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
                .addGap(0, 388, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 53, Short.MAX_VALUE))
        );

        headerSplitPane.setTopComponent(headerPanel);

        toolbarSplitPane.setDividerLocation(115);

        lPToolbar.setMaximumSize(new java.awt.Dimension(120, 32767));
        lPToolbar.setMinimumSize(new java.awt.Dimension(120, 0));
        lPToolbar.setPreferredSize(new java.awt.Dimension(120, 400));

        jLabel3.setText("lp toolbar");

        javax.swing.GroupLayout lPToolbarLayout = new javax.swing.GroupLayout(lPToolbar);
        lPToolbar.setLayout(lPToolbarLayout);
        lPToolbarLayout.setHorizontalGroup(
            lPToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lPToolbarLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 75, Short.MAX_VALUE))
        );
        lPToolbarLayout.setVerticalGroup(
            lPToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lPToolbarLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        toolbarSplitPane.setLeftComponent(lPToolbar);

        databaseToolbar.setMaximumSize(new java.awt.Dimension(120, 32767));
        databaseToolbar.setMinimumSize(new java.awt.Dimension(120, 0));
        databaseToolbar.setPreferredSize(new java.awt.Dimension(120, 400));

        jLabel7.setText("database toolbar");

        javax.swing.GroupLayout databaseToolbarLayout = new javax.swing.GroupLayout(databaseToolbar);
        databaseToolbar.setLayout(databaseToolbarLayout);
        databaseToolbarLayout.setHorizontalGroup(
            databaseToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseToolbarLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 38, Short.MAX_VALUE))
        );
        databaseToolbarLayout.setVerticalGroup(
            databaseToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseToolbarLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        toolbarSplitPane.setLeftComponent(databaseToolbar);

        userToolbar.setMaximumSize(new java.awt.Dimension(120, 32767));
        userToolbar.setMinimumSize(new java.awt.Dimension(120, 0));
        userToolbar.setPreferredSize(new java.awt.Dimension(120, 400));

        userBack.setText("Back");
        userBack.setMargin(new java.awt.Insets(2, 8, 2, 8));
        userBack.setMaximumSize(new java.awt.Dimension(105, 23));
        userBack.setMinimumSize(new java.awt.Dimension(105, 23));
        userBack.setPreferredSize(new java.awt.Dimension(105, 23));

        javax.swing.GroupLayout userToolbarLayout = new javax.swing.GroupLayout(userToolbar);
        userToolbar.setLayout(userToolbarLayout);
        userToolbarLayout.setHorizontalGroup(
            userToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userToolbarLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(userBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        userToolbarLayout.setVerticalGroup(
            userToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userToolbarLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(userBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        toolbarSplitPane.setLeftComponent(userToolbar);

        idleToolbar.setMaximumSize(new java.awt.Dimension(120, 32767));
        idleToolbar.setMinimumSize(new java.awt.Dimension(120, 406));
        idleToolbar.setPreferredSize(new java.awt.Dimension(120, 428));

        user.setText("Set User");
        user.setToolTipText("Set the current user, either by creating or loading one in the datase");
        user.setBorder(null);
        user.setMargin(new java.awt.Insets(2, 8, 2, 8));
        user.setMaximumSize(new java.awt.Dimension(105, 23));
        user.setMinimumSize(new java.awt.Dimension(105, 23));
        user.setPreferredSize(new java.awt.Dimension(105, 23));

        newExperiment.setText("New");
        newExperiment.setToolTipText("Create a new experiment");
        newExperiment.setBorder(null);
        newExperiment.setMargin(new java.awt.Insets(2, 8, 2, 8));
        newExperiment.setMaximumSize(new java.awt.Dimension(105, 23));
        newExperiment.setMinimumSize(new java.awt.Dimension(105, 23));
        newExperiment.setPreferredSize(new java.awt.Dimension(105, 23));

        loadExperiment.setText("Load");
        loadExperiment.setToolTipText("Load an experiment from the database");
        loadExperiment.setMargin(new java.awt.Insets(2, 8, 2, 8));

        jLabel4.setText("Experiment:");

        jLabel6.setText("Batch");

        addBatch.setText("Add");

        javax.swing.GroupLayout idleToolbarLayout = new javax.swing.GroupLayout(idleToolbar);
        idleToolbar.setLayout(idleToolbarLayout);
        idleToolbarLayout.setHorizontalGroup(
            idleToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(idleToolbarLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(idleToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addBatch)
                    .addGroup(idleToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, idleToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(loadExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel4))
                    .addComponent(jLabel6))
                .addGap(5, 5, 5))
        );

        idleToolbarLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addBatch, loadExperiment, newExperiment, user});

        idleToolbarLayout.setVerticalGroup(
            idleToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(idleToolbarLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addComponent(newExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(loadExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(addBatch)
                .addContainerGap(270, Short.MAX_VALUE))
        );

        idleToolbarLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addBatch, loadExperiment, newExperiment, user});

        toolbarSplitPane.setLeftComponent(idleToolbar);

        displaySplitPane.setDividerLocation(200);
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
        textScroll.setViewportView(textArea);

        displaySplitPane.setRightComponent(textScroll);

        idlePanel.setPreferredSize(new java.awt.Dimension(32767, 32767));

        jLabel1.setText("idle panel");

        javax.swing.GroupLayout idlePanelLayout = new javax.swing.GroupLayout(idlePanel);
        idlePanel.setLayout(idlePanelLayout);
        idlePanelLayout.setHorizontalGroup(
            idlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(idlePanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        idlePanelLayout.setVerticalGroup(
            idlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(idlePanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        displaySplitPane.setLeftComponent(idlePanel);

        jLabel2.setText("lp panel");

        javax.swing.GroupLayout lPPanelLayout = new javax.swing.GroupLayout(lPPanel);
        lPPanel.setLayout(lPPanelLayout);
        lPPanelLayout.setHorizontalGroup(
            lPPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lPPanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        lPPanelLayout.setVerticalGroup(
            lPPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lPPanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        displaySplitPane.setLeftComponent(lPPanel);

        jLabel5.setText("user panel");

        javax.swing.GroupLayout userPanelLayout = new javax.swing.GroupLayout(userPanel);
        userPanel.setLayout(userPanelLayout);
        userPanelLayout.setHorizontalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        userPanelLayout.setVerticalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        displaySplitPane.setLeftComponent(userPanel);

        jLabel8.setText("Database Connection:");

        databaseURLText.setText("Database URL:");

        connectDatabase.setText("Connect");

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

        javax.swing.GroupLayout databasePanelLayout = new javax.swing.GroupLayout(databasePanel);
        databasePanel.setLayout(databasePanelLayout);
        databasePanelLayout.setHorizontalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                    .addGroup(databasePanelLayout.createSequentialGroup()
                        .addComponent(databaseURLText)
                        .addGap(5, 5, 5)
                        .addComponent(connectDatabase)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        databasePanelLayout.setVerticalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(10, 10, 10)
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseURLText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connectDatabase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        displaySplitPane.setLeftComponent(databasePanel);

        toolbarSplitPane.setRightComponent(displaySplitPane);

        headerSplitPane.setRightComponent(toolbarSplitPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    public void init() {
        //this.engine.addObserver(this);
        
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
        } catch (InstantiationException ex) {
            this.logger.severe("Look and feel instantiation exception");
        } catch (IllegalAccessException ex) {
            this.logger.severe("Illegal access of look and feel");
        } catch (UnsupportedLookAndFeelException ex) {
            this.logger.severe("Unsupported look and feel");
        } catch (ClassNotFoundException ex) {
            this.logger.severe("Class not found when setting look and feel");
        } 
        
        this.initComponents();
        this.logger.addLogger(new LogHandler(this));
    }
    
    @Override
    public void run() {
        this.setVisible(true);
        this.type = RuntimeType.RUNTIME_SELECT;
        this.validateState();
    }
    
    public void setEngine(DPADLocalEngine engine) {
        if (this.engine != null) {
            this.logger.warning("Cannot change the DPAD engine once it has been set");
            return;
        }
        
        this.engine = engine;
        this.engine.addObserver(this);
    }
    
    @Override
    public void update(Observable obs, Object obj) {
        if (obs == this.engine) {
            if (this.engine.getCurrentRuntime() != this.type) {
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
    
    public void validateState() {
        switch (this.type) {
            case RUNTIME_SELECT:    this.setCorePanels(this.databasePanel, this.databaseToolbar);
                                    break;
            
            
            
            
            case DATABASE:      this.setCorePanels(this.databasePanel, this.databaseToolbar);
                                break;
                
            case IDLE:          this.setCorePanels(this.idlePanel, this.idleToolbar);
                                if (!this.engine.hasUser) {
                                    this.newExperiment.setEnabled(false);
                                    this.loadExperiment.setEnabled(false);
                                    this.addBatch.setEnabled(false);
                                    return;
                                }

                                if (!this.engine.hasExperiment) {
                                    this.addBatch.setEnabled(false);
                                }
                                break;
            
            case USER_SELECT:   this.setCorePanels(this.userPanel, this.userToolbar);
                                break;
                
            default:            
        }
        
        this.maintainMinimumDividerSizes();
    }
    
    private void setCorePanels(JPanel panel, JPanel toolbar) {
        this.hideAllPanels();
        panel.setVisible(true);
        this.displaySplitPane.setLeftComponent(panel);
        toolbar.setVisible(true);
        this.toolbarSplitPane.setLeftComponent(toolbar);
    }
    
    private void hideAllPanels() {
        this.idlePanel.setVisible(false);
        this.idleToolbar.setVisible(false);
        
        this.databasePanel.setVisible(false);
        this.databaseToolbar.setVisible(false);
        
        this.userPanel.setVisible(false);
        this.userToolbar.setVisible(false);
        
        this.lPPanel.setVisible(false);
        this.lPToolbar.setVisible(false);
    }
    
    private void maintainMinimumDividerSizes() {
        if (this.headerSplitPane.getDividerLocation() < 70) {
            this.headerSplitPane.setDividerLocation(70);
        }
        
        if (this.toolbarSplitPane.getDividerLocation() <= 115) {
            this.toolbarSplitPane.setDividerLocation(115);
        }
        
        if (this.displaySplitPane.getDividerLocation() <= (this.displaySplitPane.getHeight() - 120)) {
            this.displaySplitPane.setDividerLocation(this.displaySplitPane.getHeight() - 120);
        }
    }
    
    public void addRuntimeSelectListener(ActionListener listener) {
        this.localRuntime.addActionListener(listener);
        this.hostRuntime.addActionListener(listener);
        this.connectRuntime.addActionListener(listener);
    }
    
    public void addMainMenuListener(ActionListener listener) {
        this.user.addActionListener(listener);
    }
    
    public void addUserRuntimeListener(ActionListener listener) {
        this.userBack.addActionListener(listener);
    }
    
    public void addDatabaseListener(ActionListener listener) {
        this.connectDatabase.addActionListener(listener);
    }
    
    public JButton load;
    public JButton analyse;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton addBatch;
    public javax.swing.JButton connectDatabase;
    public javax.swing.JPanel databasePanel;
    public javax.swing.JPanel databaseToolbar;
    public javax.swing.JTextField databaseURLText;
    public javax.swing.JSplitPane displaySplitPane;
    public javax.swing.JPanel headerPanel;
    public javax.swing.JSplitPane headerSplitPane;
    public javax.swing.JPanel idlePanel;
    public javax.swing.JPanel idleToolbar;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabel8;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextArea1;
    public javax.swing.JPanel lPPanel;
    public javax.swing.JPanel lPToolbar;
    public javax.swing.JButton loadExperiment;
    public javax.swing.JButton newExperiment;
    public javax.swing.JTextArea textArea;
    public javax.swing.JScrollPane textScroll;
    public javax.swing.JLabel title;
    public javax.swing.JSplitPane toolbarSplitPane;
    public javax.swing.JButton user;
    public javax.swing.JButton userBack;
    public javax.swing.JPanel userPanel;
    public javax.swing.JPanel userToolbar;
    // End of variables declaration//GEN-END:variables
}
