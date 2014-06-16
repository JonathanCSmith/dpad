package jonathansmith.dpad.common.gui.util;

import javax.swing.*;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Blank panel for generic GUI use.
 */
public class BlankPanel extends DisplayPanel {

    private final JPanel panel = new JPanel();

    @Override
    public JPanel getContentPane() {
        return this.panel;
    }

    @Override
    public void update() {

    }
}
