package jonathansmith.dpad.client.gui.user;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.api.common.network.session.ISessionData;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;

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

    private boolean isLoggedIn       = false;
    private boolean isDoingSomething = false;

    private JPanel  contentPane;
    private JButton logButton;
    private JButton backButton;
    private JButton userButton;
    private JButton changePasswordButton;

    public UserAdministrationToolbar(ClientEngine engine) {
        this.engine = engine;

        this.contentPane.setMaximumSize(new Dimension(100, -1));
        this.logButton.addActionListener(this);
        this.userButton.addActionListener(this);
        this.changePasswordButton.addActionListener(this);
        this.backButton.addActionListener(this);

        this.switchUserState();
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
            this.engine.getEventThread().postEvent(new UserToolbarEvent(UserToolbarEvent.ToolbarStatus.NEW_USER));
            this.isDoingSomething = true;
            this.switchDisplayState();
        }

        else if (ae.getSource() == this.logButton) {
            if (this.isLoggedIn) {
                this.engine.getEventThread().postEvent(new UserToolbarEvent(UserToolbarEvent.ToolbarStatus.LOGOUT));
            }

            else {
                this.engine.getEventThread().postEvent(new UserToolbarEvent(UserToolbarEvent.ToolbarStatus.LOGIN));
            }

            this.isDoingSomething = true;
            this.switchDisplayState();
        }

        else if (ae.getSource() == this.changePasswordButton) {
            this.engine.getEventThread().postEvent(new UserToolbarEvent(UserToolbarEvent.ToolbarStatus.CHANGE_PASSWORD));
            this.isDoingSomething = true;
            this.switchDisplayState();
        }

        else if (ae.getSource() == this.backButton) {
            if (this.isDoingSomething) {
                this.engine.getEventThread().postEvent(new UserToolbarEvent(UserToolbarEvent.ToolbarStatus.BACK));
                this.isDoingSomething = false;
                this.switchDisplayState();
            }

            else {
                this.engine.getEventThread().postEvent(new ClientDisplayChangeEvent(new ClientHomeDisplay(this.engine)));
            }
        }
    }

    private void switchUserState() {
        if (this.isLoggedIn) {
            this.userButton.setVisible(false);
            this.logButton.setText("Logout");
            this.changePasswordButton.setVisible(true);
        }

        else {
            this.userButton.setVisible(true);
            this.logButton.setText("Login");
            this.changePasswordButton.setVisible(false);
        }
    }

    private void switchDisplayState() {
        if (this.isDoingSomething) {
            this.userButton.setVisible(false);
            this.logButton.setVisible(false);
            this.changePasswordButton.setVisible(false);
        }

        else {
            this.switchUserState();
        }
    }
}
