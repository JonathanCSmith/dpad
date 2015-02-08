package jonathansmith.dpad.client.gui.experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.network.session.ISessionData;
import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

import jonathansmith.dpad.common.engine.state.ExperimentAdministrationState;
import jonathansmith.dpad.common.network.packet.play.experiment.ExperimentAdministrationPacket;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ExperimentToolbarEvent;
import jonathansmith.dpad.client.engine.executor.experiment.ExperimentServerResponseExecutor;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Experiment Panel
 */
public class ExperimentAdministrationPanel extends DisplayPanel implements IEventListener, ActionListener, ListSelectionListener {

    private static final List<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(ExperimentToolbarEvent.class);
    }

    private final ClientEngine      engine;
    private final ExperimentDisplay display;

    private boolean isRunningExperiment = false;
    private boolean isNew               = false;
    private boolean isExisting          = false;

    private JPanel     contentPane;
    private JTextField experimentName;
    private JTextField experimentDetailsTextField;
    private JTextField experimentNameTextField;
    private JButton    submitButton;
    private JList      experimentList;

    public ExperimentAdministrationPanel(ClientEngine engine, ExperimentDisplay display) {
        this.engine = engine;
        this.display = display;

        this.submitButton.addActionListener(this);
        this.experimentList.setVisible(false);
        this.experimentList.setModel(new ExperimentListModel());
        this.experimentList.getSelectionModel().addListSelectionListener(this);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {
        ISessionData data = this.engine.getSessionData();
        if (data.isRunningExperiment() != this.isRunningExperiment) {
            this.isRunningExperiment = data.isRunningExperiment();
            this.switchExperimentState(data);
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        if (!(event instanceof ExperimentToolbarEvent)) {
            return;
        }

        ExperimentToolbarEvent evt = (ExperimentToolbarEvent) event;

        switch (evt.getButtonPress()) {
            case CREATE_EXPERIMENT:
                this.isNew = true;
                break;

            case LOAD_EXPERIMENT:
                this.isExisting = true;
                break;

            case BACK:
                this.isNew = false;
                this.isExisting = false;
                break;

            case RESET:
                this.isNew = false;
                this.isExisting = false;

            default:
                break;
        }

        this.switchExperimentState(this.engine.getSessionData());
    }

    private void switchExperimentState(ISessionData data) {
        if (this.isNew) {
            this.experimentDetailsTextField.setEnabled(true);
            this.experimentDetailsTextField.setText("Enter your experimental information below:");
            this.experimentNameTextField.setEnabled(true);
            this.experimentName.setEnabled(true);
            this.experimentName.setText("");
            this.experimentName.setEditable(true);
        }

        else if (this.isExisting) {
            this.experimentDetailsTextField.setVisible(false);
            this.experimentNameTextField.setVisible(false);
            this.experimentName.setVisible(false);
            this.experimentList.setVisible(true);
        }

        else {
            this.experimentDetailsTextField.setVisible(true);
            this.experimentDetailsTextField.setText("Current experiment details:");
            this.experimentNameTextField.setVisible(true);
            this.experimentName.setVisible(true);
            this.experimentName.setEditable(false);

            if (this.isRunningExperiment) {
                this.experimentName.setText(data.getCurrentExperimentName());
            }

            else {
                this.experimentName.setText("");
            }

            this.experimentList.setVisible(false);
            this.submitButton.setVisible(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (this.isNew) {
            if (!this.experimentName.getText().contentEquals("")) {
                ExperimentRecord record = new ExperimentRecord();
                record.setExperimentName(this.experimentName.getText());

                this.engine.getEventThread().postEvent(new ExperimentToolbarEvent(ExperimentToolbarEvent.ToolbarStatus.RESET));

                this.engine.setProposedExecutor(new ExperimentServerResponseExecutor(this.engine, this.display));
                this.engine.getSession().scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.NEW_EXPERIMENT, record), new GenericFutureListener[0]);
            }
        }

        else if (this.isExisting) {
            if (this.experimentList.getSelectedIndex() < 0) {
                return;
            }

            this.engine.getEventThread().postEvent(new ExperimentToolbarEvent(ExperimentToolbarEvent.ToolbarStatus.RESET));

            ExperimentRecord record = ((ExperimentListModel) this.experimentList.getModel()).getRecord(this.experimentList.getSelectedIndex());
            this.engine.setProposedExecutor(new ExperimentServerResponseExecutor(this.engine, this.display));
            this.engine.getSession().scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.SETTING_CURRENT_EXPERIMENT, record), new GenericFutureListener[0]);
        }

        else {
            // WTF?! Cannot happen i think
        }
    }

    public void setListContents(HashSet<ExperimentRecord> listContents) {
        ((ExperimentListModel) this.experimentList.getModel()).setRecords(listContents);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel model = (ListSelectionModel) e.getSource();
        int index = e.getFirstIndex();
        if (model.isSelectedIndex(index)) {
            // TODO: Change the pane contents
        }
    }
}
