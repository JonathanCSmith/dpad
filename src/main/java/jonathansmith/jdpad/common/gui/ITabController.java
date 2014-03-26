package jonathansmith.jdpad.common.gui;

import javax.swing.*;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Methods for allowing a tab to be displayed within the core GUI
 */
public interface ITabController {

    String getTitle();

    JPanel getPanel();

    void init();

    void update();

    void onWindowClosing();

    void onWindowClosed();

    void shutdown(boolean force);
}
