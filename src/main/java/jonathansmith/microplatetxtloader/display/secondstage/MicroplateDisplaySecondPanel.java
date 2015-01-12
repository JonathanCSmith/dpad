package jonathansmith.microplatetxtloader.display.secondstage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;
import jonathansmith.microplatetxtloader.events.MicroplateLoaderSecondStageFinishEvent;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for all GUI displays
 */
public class MicroplateDisplaySecondPanel extends DisplayPanel implements IEventListener, ActionListener {

    private static final ArrayList<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(MicroplateLoaderSecondStageFinishEvent.class);
    }

    private final MicroplateTXTLoader core;
    private final IPluginRuntime      runtime;

    private SampleTableModel tableModel;

    private JPanel      contentPane;
    private JTextPane   pleaseEnterYourSampleTextPane;
    private JTable      table;
    private JButton     doneButton;
    private JScrollPane scrollPane;

    public MicroplateDisplaySecondPanel(MicroplateTXTLoader core, IPluginRuntime runtime) {
        this.core = core;
        this.runtime = runtime;

        this.tableModel = new SampleTableModel(this.core);
        this.table.setModel(this.tableModel);

        this.doneButton.addActionListener(this);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        this.doneButton.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.doneButton) {
            this.scrollPane.setVisible(false);
            this.table.setVisible(false);
            this.runtime.getEventThread().postEvent(new MicroplateLoaderSecondStageFinishEvent(this.tableModel.getValues()));
            this.doneButton.setEnabled(false);
        }
    }
}
