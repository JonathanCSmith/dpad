package jonathansmith.dpad.client.gui.home;

import javax.swing.*;

import jonathansmith.dpad.api.client.session.ISessionData;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Represents the core home GUI for the client. Current session information is displayed here.
 */
public class ClientHomePanel extends DisplayPanel {

    private final ClientEngine engine;

    private JPanel     contentPane;
    private JTextField userName;
    private JTextField userValue;

    public ClientHomePanel(ClientEngine engine) {
        this.engine = engine;
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {
        ISessionData data = this.engine.getSessionData();
        if (data.isUserLoggedIn()) {
            this.userValue.setText(data.getUserName());
        }

        else {
            this.userValue.setText("");
        }
    }
}
