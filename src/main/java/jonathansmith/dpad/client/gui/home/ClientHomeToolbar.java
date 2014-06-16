package jonathansmith.dpad.client.gui.home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.api.client.session.ISessionData;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Represents the core client GUI toolbar
 */
public class ClientHomeToolbar extends DisplayPanel implements ActionListener {

    private final ClientEngine engine;

    private boolean isLoggedIn = true;

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
        ISessionData data = this.engine.getSessionData();
        if (data.isUserLoggedIn() != this.isLoggedIn) {
            if (data.isUserLoggedIn()) {
                this.userButton.setText("User Logout");
                this.isLoggedIn = true;
            }

            else {
                this.userButton.setText("User Login");
                this.isLoggedIn = false;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.userButton) {
            if (this.isLoggedIn) {
                // TODO: Send user logout packet!
            }

            else {
                // TODO: Build user GUI
            }
        }
    }
}
