package jonathansmith.dpad.server.gui.home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.engine.event.ServerDisplayChangeEvent;
import jonathansmith.dpad.server.gui.configuration.ServerConfigurationDisplay;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Server Home toolbar
 */
public class ServerHomeToolbar extends DisplayPanel implements ActionListener {

    private final ServerEngine engine;

    private JPanel  contentPane;
    private JButton configuration;

    public ServerHomeToolbar(ServerEngine engine) {
        this.engine = engine;
        this.configuration.addActionListener(this);
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
        if (ae.getSource() == this.configuration) {
            this.engine.getEventThread().postEvent(new ServerDisplayChangeEvent(new ServerConfigurationDisplay(this.engine)));
        }
    }
}
