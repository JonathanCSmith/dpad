package jonathansmith.dpad.common.gui.util;

import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Generic progress bar display.
 */
public class ProgressPanel extends DisplayPanel implements IEventListener {

    private static List<Class<? extends Event>> EVENTS = new LinkedList<Class<? extends Event>>();

    private JPanel       contentPane;
    private JTextField   progressText;
    private JProgressBar progressBar;

    private ProgressBarUpdateEvent newProgress;

    public ProgressPanel() {
        this.contentPane.setVisible(true);
        this.progressText.setVisible(true);
        this.progressBar.setVisible(true);
    }

    public void update() {
        if (this.newProgress != null) {
            this.progressBar.setMinimum(this.newProgress.getProgressMinimum());
            this.progressBar.setMaximum(this.newProgress.getProgressMaximum());
            this.progressBar.setValue(this.newProgress.getProgress());
            this.progressText.setText(this.newProgress.getProgressBarText() + ": " + this.newProgress.getProgress() + " / " + this.newProgress.getProgressMaximum());
            this.newProgress = null;
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        int index = EVENTS.indexOf(event.getClass());

        switch (index) {
            case -1:
                return;

            case 0:
                this.newProgress = (ProgressBarUpdateEvent) event;
                return;
        }
    }

    static {
        EVENTS.add(ProgressBarUpdateEvent.class);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }
}
