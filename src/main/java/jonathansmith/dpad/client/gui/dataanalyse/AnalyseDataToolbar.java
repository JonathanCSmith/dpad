package jonathansmith.dpad.client.gui.dataanalyse;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.LoadDataToolbarEvent;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Load Data toolbar
 */
public class AnalyseDataToolbar extends DisplayPanel implements ActionListener {

    private final ClientEngine engine;

    private JPanel  contentPane;
    private JButton selectPluginButton;
    private JButton backButton;

    public AnalyseDataToolbar(ClientEngine engine) {
        this.engine = engine;

        this.contentPane.setMaximumSize(new Dimension(100, -1));
        this.selectPluginButton.addActionListener(this);
        this.backButton.addActionListener(this);
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
        if (ae.getSource() == this.selectPluginButton) {
            this.engine.getEventThread().postEvent(new LoadDataToolbarEvent(LoadDataToolbarEvent.ToolbarStatus.REFRESH_PLUGINS));
        }

        else if (ae.getSource() == this.backButton) {
            this.engine.getEventThread().postEvent(new LoadDataToolbarEvent(LoadDataToolbarEvent.ToolbarStatus.BACK));
        }
    }
}
