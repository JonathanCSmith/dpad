package jonathansmith.dpad.client.gui.home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.user.UserDisplay;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Represents the core client GUI toolbar
 */
public class ClientHomeToolbar extends DisplayPanel implements ActionListener {

    private final ClientEngine engine;

    private JPanel  contentPane;
    private JButton userButton;

    public ClientHomeToolbar(ClientEngine engine) {
        this.engine = engine;

        this.userButton.addActionListener(this);
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
        if (ae.getSource() == this.userButton) {
            this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new UserDisplay(this.engine)));
        }
    }
}
