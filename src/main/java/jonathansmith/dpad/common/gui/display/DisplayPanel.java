package jonathansmith.dpad.common.gui.display;

import javax.swing.*;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Display panel is used by all internal displays
 */
public abstract class DisplayPanel {

    public abstract JPanel getContentPane();

    public abstract void update();

    public void showModal(String modalText) {
        JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this.getContentPane()), modalText);
    }
}
