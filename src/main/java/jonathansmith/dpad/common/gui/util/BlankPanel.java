package jonathansmith.dpad.common.gui.util;

import javax.swing.*;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for all GUI displays
 */
public class BlankPanel extends DisplayPanel {

    private JPanel contentPane;

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }
}
