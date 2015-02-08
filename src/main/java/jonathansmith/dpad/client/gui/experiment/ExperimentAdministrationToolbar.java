package jonathansmith.dpad.client.gui.experiment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

import jonathansmith.dpad.common.engine.state.ExperimentAdministrationState;
import jonathansmith.dpad.common.network.packet.play.experiment.ExperimentAdministrationPacket;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.engine.event.ExperimentToolbarEvent;
import jonathansmith.dpad.client.engine.executor.experiment.ExperimentServerResponseExecutor;
import jonathansmith.dpad.client.gui.home.ClientHomeDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Experiment Toolbar
 */
public class ExperimentAdministrationToolbar extends DisplayPanel implements ActionListener, IEventListener {

    private static final ArrayList<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(ExperimentToolbarEvent.class);
    }

    private final ClientEngine      engine;
    private final ExperimentDisplay display;

    private boolean isAdministrating = false;

    private JPanel  contentPane;
    private JButton createExperimentButton;
    private JButton backButton;
    private JButton loadExperimentButton;

    public ExperimentAdministrationToolbar(ClientEngine engine, ExperimentDisplay display) {
        this.engine = engine;
        this.display = display;

        this.contentPane.setMaximumSize(new Dimension(100, -1));
        this.createExperimentButton.addActionListener(this);
        this.loadExperimentButton.addActionListener(this);
        this.backButton.addActionListener(this);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.createExperimentButton) {
            this.engine.getEventThread().postEvent(new ExperimentToolbarEvent(ExperimentToolbarEvent.ToolbarStatus.CREATE_EXPERIMENT));
            this.isAdministrating = true;
            this.createExperimentButton.setVisible(false);
            this.loadExperimentButton.setVisible(false);
        }

        else if (ae.getSource() == this.loadExperimentButton) {
            this.engine.getEventThread().postEvent(new ExperimentToolbarEvent(ExperimentToolbarEvent.ToolbarStatus.LOAD_EXPERIMENT));
            this.isAdministrating = true;
            this.createExperimentButton.setVisible(false);
            this.loadExperimentButton.setVisible(false);

            this.engine.getSession().scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.REQUESTING_EXPERIMENTS), new GenericFutureListener[0]);
            this.engine.setProposedExecutor(new ExperimentServerResponseExecutor(this.engine, this.display));
        }

        else if (ae.getSource() == this.backButton) {
            if (this.isAdministrating) {
                this.engine.getEventThread().postEvent(new ExperimentToolbarEvent(ExperimentToolbarEvent.ToolbarStatus.BACK));
                this.isAdministrating = false;

                this.createExperimentButton.setVisible(true);
                this.loadExperimentButton.setVisible(true);
            }

            else {
                this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ClientHomeDisplay(this.engine)));
            }
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        ExperimentToolbarEvent evt = (ExperimentToolbarEvent) event;
        if (evt.getButtonPress() == ExperimentToolbarEvent.ToolbarStatus.RESET) {
            this.isAdministrating = false;

            this.createExperimentButton.setVisible(true);
            this.loadExperimentButton.setVisible(true);
        }
    }
}
