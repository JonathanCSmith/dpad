package jonathansmith.dpad.server.gui.home;

import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.common.engine.event.IEventListener;

import jonathansmith.dpad.common.engine.event.Event;
import jonathansmith.dpad.common.gui.display.DisplayPanel;

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
