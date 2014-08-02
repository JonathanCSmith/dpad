package jonathansmith.dpad.client.gui.user;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.api.common.network.session.ISessionData;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.engine.event.UserToolbarEvent;
import jonathansmith.dpad.client.gui.home.ClientHomeDisplay;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * User Administration Controls
 */
public class UserAdministrationToolbar extends DisplayPanel implements ActionListener {

    private final ClientEngine engine;

    private boolean isLoggedIn           = false;
    private boolean isAdministrationMode = false;

    private JPanel  contentPane;
    private JButton userButton;
    private JButton backButton;
    private JButton administrateButton;

    public UserAdministrationToolbar(ClientEngine engine) {
        this.engine = engine;

        this.contentPane.setMaximumSize(new Dimension(100, -1));
        this.userButton.addActionListener(this);
        this.administrateButton.addActionListener(this);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {
        ISessionData data = this.engine.getSessionData();
        if (data.isUserLoggedIn() != this.isLoggedIn) {
            this.isLoggedIn = data.isUserLoggedIn();
            this.switchUserState();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.userButton) {
            this.engine.getEventThread().postEvent(new UserToolbarEvent((byte) 0));
        }

        else if (ae.getSource() == this.administrateButton) {
            this.engine.getEventThread().postEvent(new UserToolbarEvent((byte) 1));
            this.isAdministrationMode = true;
            this.switchUserState();
        }

        else if (ae.getSource() == this.backButton) {
            if (this.isAdministrationMode) {
                this.engine.getEventThread().postEvent(new UserToolbarEvent((byte) 2));
                this.isAdministrationMode = false;
                this.switchUserState();
            }

            else {
                this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ClientHomeDisplay(this.engine)));
            }
        }
    }

    private void switchUserState() {
        if (this.isAdministrationMode) {
            if (this.isLoggedIn) {
                this.userButton.setText("Submit");
                this.administrateButton.setEnabled(false);
            }

            else {
                this.userButton.setText("Submit");
                this.administrateButton.setEnabled(false);
            }
        }

        else {
            if (this.isLoggedIn) {
                this.userButton.setText("Logout");
                this.administrateButton.setEnabled(true);
                this.administrateButton.setText("Change Password");
            }

            else {
                this.userButton.setText("Login");
                this.administrateButton.setEnabled(true);
                this.administrateButton.setText("New User");
            }
        }
    }
}
