package jonathansmith.dpad.common.gui.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for toolbars.
 */
public class ButtonToolbar extends DisplayPanel implements ActionListener {

    private JPanel  contentPane;
    private JButton createExperimentButton;

    public ButtonToolbar() {
        this.contentPane.setMaximumSize(new Dimension(100, -1));
        this.createExperimentButton.addActionListener(this);
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

    }
}
