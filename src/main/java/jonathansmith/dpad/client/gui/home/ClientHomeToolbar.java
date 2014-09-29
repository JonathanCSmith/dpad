package jonathansmith.dpad.client.gui.home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.api.common.network.session.ISessionData;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.dataanalyse.AnalyseDataDisplay;
import jonathansmith.dpad.client.gui.dataload.LoadDataDisplay;
import jonathansmith.dpad.client.gui.experiment.ExperimentDisplay;
import jonathansmith.dpad.client.gui.user.UserDisplay;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Represents the core client GUI toolbar
 */
public class ClientHomeToolbar extends DisplayPanel implements ActionListener {

    private final ClientEngine engine;

    private boolean isLoggedIn          = false;
    private boolean isRunningExperiment = false;

    private JPanel  contentPane;
    private JButton userButton;
    private JButton experimentAdminButton;
    private JButton loadDataButton;
    private JButton analyseDataButton;

    public ClientHomeToolbar(ClientEngine engine) {
        this.engine = engine;

        this.userButton.addActionListener(this);
        this.experimentAdminButton.addActionListener(this);
        this.loadDataButton.addActionListener(this);
        this.analyseDataButton.addActionListener(this);

        this.switchDisplayType();
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {
        ISessionData data = this.engine.getSessionData();
        if (this.isLoggedIn != data.isUserLoggedIn()) {
            this.isLoggedIn = data.isUserLoggedIn();
            this.switchDisplayType();
        }

        if (this.isRunningExperiment != data.isRunningExperiment()) {
            this.isRunningExperiment = data.isRunningExperiment();
            this.switchDisplayType();
        }
    }

    private void switchDisplayType() {
        if (!this.isLoggedIn) {
            this.experimentAdminButton.setEnabled(false);
            this.loadDataButton.setEnabled(false);
            this.analyseDataButton.setEnabled(false);
        }

        else if (!this.isRunningExperiment) {
            this.experimentAdminButton.setEnabled(true);
            this.loadDataButton.setEnabled(false);
            this.analyseDataButton.setEnabled(true);
        }

        else {
            this.experimentAdminButton.setEnabled(true);
            this.loadDataButton.setEnabled(true);
            this.analyseDataButton.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.userButton) {
            this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new UserDisplay(this.engine)));
        }

        else if (ae.getSource() == this.experimentAdminButton) {
            this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ExperimentDisplay(this.engine)));
        }

        else if (ae.getSource() == this.loadDataButton) {
            this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new LoadDataDisplay(this.engine)));
        }

        else if (ae.getSource() == this.analyseDataButton) {
            this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new AnalyseDataDisplay(this.engine)));
        }
    }
}
