package jonathansmith.dpad.client.gui.disconnect;

import javax.swing.*;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Client disconnect gui panel
 */
public class DisconnectPanel extends DisplayPanel {

    private static final String DISCONNECT_PREFIX = "You were disconnected from the server for the following reason:";

    private JTextArea disconnectText;
    private JPanel    contentPane;

    public DisconnectPanel(String reason) {
        this.disconnectText.setText(DISCONNECT_PREFIX + "\n\n" + reason);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }
}
