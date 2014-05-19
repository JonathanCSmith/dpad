package jonathansmith.dpad.api.common.gui;

/**
 * Created by Jon on 19/05/14.
 * <p/>
 * Core GUI Controller class allowing tabs to be added for additional information.
 */
public interface IGUIController {

    /**
     * Method to add a tab to the current GUI
     *
     * @param tab to add
     */
    void addTab(ITabController tab);

    /**
     * Method to remove a tab from the current GUI. Note CORE Tabs (i.e. client and server) cannot be removed.
     *
     * @param tab to remove
     */
    void removeTab(ITabController tab);
}
