package jonathansmith.kellycharacterisationwithod.display.selection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.kellycharacterisationwithod.KellyCharacterisationWithOD;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for toolbars.
 */
public class DataSelectionToolbarPanel extends DisplayPanel implements ActionListener, IEventListener {

    private static final ArrayList<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        //EVENTS.add(KellySelectDataFinishEvent.class);
    }

    private final KellyCharacterisationWithOD parent;
    private final IPluginRuntime              runtime;

    private JPanel  contentPane;
    private JButton quitButton;

    public DataSelectionToolbarPanel(KellyCharacterisationWithOD parent, IPluginRuntime runtime) {
        this.parent = parent;
        this.runtime = runtime;

        this.contentPane.setMaximumSize(new Dimension(100, -1));
        this.quitButton.addActionListener(this);
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
        if (ae.getSource() == this.quitButton) {
            this.parent.quitEarly();
            //this.runtime.getEventThread().postEvent(new KellySelectDataFinishEvent(null));
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
//        if (event instanceof KellySelectDataFinishEvent) {
//            this.quitButton.setEnabled(false);
//        }
    }
}
