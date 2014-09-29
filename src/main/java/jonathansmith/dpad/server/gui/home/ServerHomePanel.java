package jonathansmith.dpad.server.gui.home;

import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Core GUI for the server
 */
public class ServerHomePanel extends DisplayPanel implements IEventListener {

    private JPanel contentPane;

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return null;
    }

    @Override
    public void onEventReceived(Event event) {

    }
}
