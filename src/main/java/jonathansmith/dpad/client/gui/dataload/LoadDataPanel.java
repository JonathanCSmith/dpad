package jonathansmith.dpad.client.gui.dataload;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jonathansmith.dpad.api.database.LoadingPluginRecord;
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
public class LoadDataPanel extends DisplayPanel implements IEventListener, ListSelectionListener, ActionListener {

    private static final List<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(LoadDataToolbarEvent.class);
    }

    private final ClientEngine                 engine;
    private final LoadDataDisplay              display;
    private final LoaderDataListSelectionModel listModel;

    private JPanel  contentPane;
    private JList   displayList;
    private JButton submitButton;

    private RecordList<LoadingPluginRecord> plugins;

    public LoadDataPanel(ClientEngine engine, LoadDataDisplay display) {
        this.engine = engine;
        this.display = display;

        this.listModel = new LoaderDataListSelectionModel();
        this.displayList.setModel(this.listModel);
        this.engine.getPluginManager().markPendingUpdates();
        this.plugins = this.engine.getPluginManager().getLoaderPluginRecordList();
        this.listModel.clearContents();

        for (LoadingPluginRecord record : this.plugins) {
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
                    this.engine.setProposedExecutor(new PluginSelectionExecutor(this.engine, PluginSelectionExecutor.PluginType.LOADING, this.display));
                }

                // TODO: Remove
                else {
                    this.showModal("Plugins are located at: " + this.engine.getFileSystem().getPluginDirectory() + " if you wish to add plugins do before pressing ok!.");
                    this.engine.getPluginManager().markPendingUpdates();
                    this.plugins = this.engine.getPluginManager().getLoaderPluginRecordList();
                    this.listModel.clearContents();

                    for (LoadingPluginRecord record : this.plugins) {
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
                LoadingPluginRecord plugin = null;
                for (LoadingPluginRecord record : this.plugins) {
                    if (record.getPluginName().contentEquals(pluginName)) {
                        plugin = record;
                        break;
                    }
                }

                if (plugin != null) {
                    this.engine.setProposedExecutor(new PluginRuntimeExecutor(this.engine, plugin));
                }
            }
        }
    }
}
