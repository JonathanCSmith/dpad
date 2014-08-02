package jonathansmith.dpad.common.gui.util;

import java.awt.*;

import javax.swing.*;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for toolbars.
 */
public class BlankToolbar extends DisplayPanel {
    private JPanel contentPane;

    public BlankToolbar() {
        this.contentPane.setMaximumSize(new Dimension(100, -1));
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }
}
