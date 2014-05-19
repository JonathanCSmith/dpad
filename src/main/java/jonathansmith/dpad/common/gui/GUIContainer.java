package jonathansmith.dpad.common.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

import javax.swing.*;

import jonathansmith.dpad.api.common.gui.IGUIController;
import jonathansmith.dpad.api.common.gui.ITabController;

import jonathansmith.dpad.client.gui.ClientTabController;

import jonathansmith.dpad.server.gui.ServerTabController;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Parent GUI Container - contains style and layout information for all child GUI panes
 */
public class GUIContainer extends JFrame implements IGUIController, WindowListener, ActionListener, Runnable {

    private static final int MINIMUM_WIDTH  = 500;
    private static final int MINIMUM_HEIGHT = 300;

    private final DPAD                       main;
    private final LinkedList<ITabController> tabs;

    private JPanel      display;
    private JTabbedPane tabContainer;
    private JButton     shutdownAll;

    public GUIContainer(DPAD main) {
        this.main = main;
        this.tabs = new LinkedList<ITabController>();
        this.setContentPane(this.display);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
        this.addWindowListener(this);
        this.shutdownAll.addActionListener(this);
    }

    public void init() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        SwingUtilities.invokeLater(this);
    }

    @Override
    public void run() {
        for (ITabController tab : this.tabs) {
            tab.update();
        }
    }

    @Override
    public void addTab(ITabController tab) {
        if (this.tabs.contains(tab)) {
            return;
        }

        this.tabs.add(tab);
        tab.init();
        this.tabContainer.addTab(tab.getTitle(), tab.getPanel());
    }

    @Override
    public void removeTab(ITabController tab) {
        if (!this.tabs.contains(tab)) {
            return;
        }

        if (tab instanceof ClientTabController || tab instanceof ServerTabController) {
            // TODO: Log
            return;
        }

        int index = this.tabs.indexOf(tab);
        this.tabs.remove(tab);
        this.tabContainer.removeTabAt(index);
    }

    public void removeCoreTab(ITabController tab) {
        if (!(tab instanceof ClientTabController) && !(tab instanceof ServerTabController)) {
            // TODO: Log
            return;
        }

        int index = this.tabs.indexOf(tab);
        this.tabs.remove(tab);
        this.tabContainer.removeTabAt(index);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // NO-OP
    }

    @Override
    public void windowClosing(WindowEvent e) {
        for (ITabController tab : this.tabs) {
            tab.onWindowClosing();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        for (ITabController tab : this.tabs) {
            tab.onWindowClosed();
        }

        this.main.shutdownAll();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // NO-OP
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // NO-OP
    }

    @Override
    public void windowActivated(WindowEvent e) {
        // NO-OP
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // NO-OP
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.shutdownAll) {
            this.main.shutdownAll();
        }
    }
}
