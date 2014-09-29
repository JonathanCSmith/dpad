package jonathansmith.dpad.client.gui.plugin;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for all GUI displays
 */
public class PluginTemporaryDisplayPanel extends DisplayPanel {

    private JPanel     contentPane;
    private JTextField pleaseWaitWhileYourTextField;

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }
}
