package jonathansmith.dpad.common.gui.util;

import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.common.engine.event.IEventListener;

import jonathansmith.dpad.common.engine.event.Event;
import jonathansmith.dpad.common.engine.event.gui.ProgressBarUpdateEvent;
import jonathansmith.dpad.common.gui.IContentPane;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Generic progress bar display. Used for engine startups.
 * Possible that this could be generified to just a progress display (TODO?)
 */
public class ProgressPanel implements IEventListener, IContentPane {

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
