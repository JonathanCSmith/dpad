package jonathansmith.microplatetxtloader.display.firststage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.microplatetxtloader.events.MicroplateLoaderFilesDisplayEvent;
import jonathansmith.microplatetxtloader.events.MicroplateLoaderFinishFileSelectionEvent;
import jonathansmith.microplatetxtloader.events.MicroplateLoaderFirstStageFinishEvent;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for all GUI displays
 */
public class MicroplateDisplayFirstPanel extends DisplayPanel implements IEventListener, ActionListener {

    private static final ArrayList<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(MicroplateLoaderFilesDisplayEvent.class);
    }

    private final LinkedList<File> file_list = new LinkedList<File>();

    private final IPluginRuntime     runtime;
    private final MicroplateFileList listModel;

    private DisplayState state = DisplayState.FILES;

    private LinkedList<Integer> times;
    private String lastKnownLocation = "";

    private JPanel          contentPane;
    private JTextPane       pleaseAddAllOfTextPane;
    private JList           fileDisplayList;
    private JTable          table;
    private TimesTableModel timesModel;
    private JButton         submitButton;
    private JScrollPane     fileListScrollPane;
    private JScrollPane     timesListScrollPane;

    public MicroplateDisplayFirstPanel(IPluginRuntime runtime) {
        this.runtime = runtime;

        this.listModel = new MicroplateFileList();
        this.fileDisplayList.setModel(this.listModel);

        this.timesListScrollPane.setVisible(false);
        this.table.setVisible(false);
        this.timesModel = new TimesTableModel();
        this.table.setModel(this.timesModel);

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
        switch (((MicroplateLoaderFilesDisplayEvent) event).getStatus()) {
            case ADD_FILES:
                JFileChooser chooser;
                if (this.lastKnownLocation.contentEquals("")) {
                    chooser = new JFileChooser();
                }

                else {
                    chooser = new JFileChooser(this.lastKnownLocation);
                }

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "text");
                chooser.setFileFilter(filter);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(true);

                int returnValue = chooser.showOpenDialog(this.contentPane);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File[] files = chooser.getSelectedFiles();

                    for (File potentialFile : files) {
                        if (!this.file_list.contains(potentialFile)) {
                            this.file_list.add(potentialFile);
                        }
                    }

                    this.lastKnownLocation = files[0].getParentFile().getAbsolutePath();
                }

                this.listModel.updateFiles(this.file_list);
                break;

            case REMOVE_FILES:
                int[] values = this.fileDisplayList.getSelectedIndices();
                for (int i : values) {
                    this.file_list.remove(i);
                }

                this.listModel.updateFiles(this.file_list);
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.submitButton && this.state == DisplayState.FILES) {
            if (this.file_list.size() <= 0) {
                this.showModal("No files were chosen!");
            }

            else {
                this.state = DisplayState.TIMES;
                this.fileDisplayList.setVisible(false);
                this.fileListScrollPane.setVisible(false);
                this.pleaseAddAllOfTextPane.setText("Please enter the cumulative time (in minutes) of observation for each file below:");
                this.timesListScrollPane.setVisible(true);
                this.timesModel.setFiles(this.file_list);
                this.table.setVisible(true);
                this.submitButton.setText("Submit");
                this.runtime.getEventThread().postEvent(new MicroplateLoaderFinishFileSelectionEvent());
            }
        }

        else if (ae.getSource() == this.submitButton && this.state == DisplayState.TIMES) {
            if (this.timesModel.containsInvalidTimes()) {
                this.showModal("Some invalid times were present. Times must be numbers, not left empty and contain no duplicate times!");
            }

            else {
                this.times = this.timesModel.getTimes();
                this.runtime.getEventThread().postEvent(new MicroplateLoaderFirstStageFinishEvent(this.file_list, this.times));
                this.submitButton.setVisible(false);
            }
        }
    }

    private enum DisplayState {
        TIMES, FILES
    }
}
