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
package net.jonathansmith.javadpad.client.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import java.util.EventObject;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import net.jonathansmith.javadpad.client.Client;
import net.jonathansmith.javadpad.client.threads.ClientRuntimeThread;
import net.jonathansmith.javadpad.common.events.ChangeListener;
import net.jonathansmith.javadpad.common.events.gui.ContentChangedEvent;
import net.jonathansmith.javadpad.common.events.thread.ThreadChangeEvent;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;

/**
 *
 * @author Jonathan Smith
 */
public class ClientGUI extends TabbedGUI implements ChangeListener {

    public final Client engine;
    
    private final CopyOnWriteArrayList<ChangeListener> listeners;
    
    private ClientRuntimeThread currentRuntime;
    private int[] textFieldLength = new int[1024];
    private int currentTextLength = 0;
    
    /**
     * Creates new form ClientGUI
     */
    public ClientGUI(Client client) {
        this.engine = client;
        this.listeners = new CopyOnWriteArrayList<ChangeListener> ();
    }
    
    public void validateState() {
        if (this.currentRuntime.isDisplayable()) {
            DisplayOption option = this.currentRuntime.getDisplay();
            option.validateState();
            this.setCorePanels(option.getCurrentView(), option.getCurrentToolbar());
            this.fireChange(new ContentChangedEvent(this));
        }
    }
    
    private void setCorePanels(JPanel panel, JPanel toolbar) {
        this.displaySplitPane.setLeftComponent(panel);
        this.toolbarSplitPane.setLeftComponent(toolbar);
    }
    
    @Override
    public void init() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
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
        
        DisplayOption option;
        for (ClientRuntimeThread runtime : ClientRuntimeThread.values()) {
            if (runtime.isDisplayable()) {
                option = runtime.getDisplay();
                option.setController(this.engine);
            }
        }
    }
    
    @Override
    public void run() {
        this.setVisible(true);
        EventQueue.invokeLater(this);
//        this.type = RuntimeType.SETUP_CLIENT;
//        this.validateState();
    }
     
    @Override
    public void updateLog(String message) {
        this.textArea.append(message);
        
        int oldLength = this.textArea.getDocument().getLength();
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        int addedSize = this.textArea.getDocument().getLength() - oldLength;
        
        if (this.textFieldLength[this.currentTextLength] != 0) {
            this.textArea.replaceRange("", 0, this.textFieldLength[this.currentTextLength]);
        }
        
        this.textFieldLength[this.currentTextLength] = addedSize;
        this.currentTextLength = (this.currentTextLength + 1) % 1024;
    }
    
    @Override
    public void changeEventReceived(EventObject event) {
        if (event instanceof ThreadChangeEvent) {
            ClientRuntimeThread thread = (ClientRuntimeThread) event.getSource();
            if (this.currentRuntime != thread && thread.isDisplayable()) {
                this.currentRuntime = thread;
                this.validateState();
            }
        }
    }    
    
    public void addListener(ChangeListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void fireChange(EventObject event) {
        for (ChangeListener listener : this.listeners) {
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

        headerSplitPane = new JSplitPane();
        headerPanel = new JPanel();
        title = new JLabel();
        toolbarSplitPane = new JSplitPane();
        displaySplitPane = new JSplitPane();
        textScroll = new JScrollPane();
        textArea = new JTextArea();

        headerSplitPane.setDividerLocation(70);
        headerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        headerPanel.setMaximumSize(new Dimension(32767, 70));
        headerPanel.setMinimumSize(new Dimension(0, 70));

        title.setFont(new Font("Tahoma", 1, 14)); // NOI18N
        title.setText("Data Processing Analysis and Display :");

        GroupLayout headerPanelLayout = new GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 394, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 53, Short.MAX_VALUE))
        );

        headerSplitPane.setTopComponent(headerPanel);

        toolbarSplitPane.setDividerLocation(115);

        displaySplitPane.setDividerLocation(240);
        displaySplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        displaySplitPane.setResizeWeight(0.5);
        displaySplitPane.setAutoscrolls(true);
        displaySplitPane.setPreferredSize(new Dimension(522, 228));

        textScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        textScroll.setMinimumSize(new Dimension(140, 23));
        textScroll.setPreferredSize(new Dimension(140, 22));

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText("DPAD Console Log:");
        textArea.setMaximumSize(null);
        textArea.setMinimumSize(null);
        textArea.setPreferredSize(null);
        textScroll.setViewportView(textArea);

        displaySplitPane.setRightComponent(textScroll);

        toolbarSplitPane.setRightComponent(displaySplitPane);

        headerSplitPane.setRightComponent(toolbarSplitPane);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(headerSplitPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(headerSplitPane, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JSplitPane displaySplitPane;
    public JPanel headerPanel;
    public JSplitPane headerSplitPane;
    public JTextArea textArea;
    public JScrollPane textScroll;
    public JLabel title;
    public JSplitPane toolbarSplitPane;
    // End of variables declaration//GEN-END:variables
}