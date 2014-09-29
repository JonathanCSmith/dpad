package jonathansmith.dpad.client.gui.dataanalyse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jonathansmith.dpad.api.database.AnalysingPluginRecord;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

import jonathansmith.dpad.common.database.util.RecordList;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.LoadDataToolbarEvent;
import jonathansmith.dpad.client.engine.executor.pluginruntime.PluginRuntimeExecutor;
import jonathansmith.dpad.client.engine.executor.pluginselection.PluginSelectionExecutor;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Load data panel
 */
public class AnalyseDataPanel extends DisplayPanel implements IEventListener, ListSelectionListener, ActionListener {

    private static final List<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(LoadDataToolbarEvent.class);
    }

    private final ClientEngine                   engine;
    private final AnalyseDataDisplay             display;
    private final AnalyserDataListSelectionModel listModel;

    private JPanel  contentPane;
    private JList   displayList;
    private JButton submitButton;

    private RecordList<AnalysingPluginRecord> plugins;

    public AnalyseDataPanel(ClientEngine engine, AnalyseDataDisplay display) {
        this.engine = engine;
        this.display = display;

        this.listModel = new AnalyserDataListSelectionModel();
        this.displayList.setModel(this.listModel);
        this.engine.getPluginManager().markPendingUpdates();
        this.plugins = this.engine.getPluginManager().getAnalyserPluginRecordList();
        this.listModel.clearContents();

        for (AnalysingPluginRecord record : this.plugins) {
            this.listModel.addElement(record.getPluginName());
        }
        this.displayList.getSelectionModel().addListSelectionListener(this);
        this.submitButton.addActionListener(this);
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
        switch (((LoadDataToolbarEvent) event).getButtonPress()) {
            case REFRESH_PLUGINS:
                // TODO: Convert to networkable plugin infrastructure
                if (false) {
                    this.engine.setAndWaitForProposedExecutor(new PluginSelectionExecutor(this.engine, PluginSelectionExecutor.PluginType.ANALYSING, this.display));
                }

                // TODO: Remove
                else {
                    this.showModal("Plugins are located at: " + this.engine.getFileSystem().getPluginDirectory() + " if you wish to add plugins do before pressing ok!.");
                    this.engine.getPluginManager().markPendingUpdates();
                    this.plugins = this.engine.getPluginManager().getAnalyserPluginRecordList();
                    this.listModel.clearContents();

                    for (AnalysingPluginRecord record : this.plugins) {
                        this.listModel.addElement(record.getPluginName());
                    }
                }

                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel model = (ListSelectionModel) e.getSource();
        int index = e.getFirstIndex();
        if (model.isSelectedIndex(index)) {
            // TODO: Change the pane contents
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.submitButton) {
            int index = this.displayList.getSelectedIndex();
            if (index != -1) {
                String pluginName = this.listModel.getElementAt(index);
                AnalysingPluginRecord plugin = null;
                for (AnalysingPluginRecord record : this.plugins) {
                    if (record.getPluginName().contentEquals(pluginName)) {
                        plugin = record;
                        break;
                    }
                }

                if (plugin != null) {
                    this.engine.setAndWaitForProposedExecutor(new PluginRuntimeExecutor(this.engine, plugin));
                }
            }
        }
    }
}
