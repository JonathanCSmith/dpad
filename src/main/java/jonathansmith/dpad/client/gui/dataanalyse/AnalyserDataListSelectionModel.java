package jonathansmith.dpad.client.gui.dataanalyse;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * Created by Jon on 18/09/2014.
 * <p/>
 * Loader data selection model implementation
 */
public class AnalyserDataListSelectionModel extends AbstractListModel<String> {

    public List<String> stringList = new ArrayList<String>();

    @Override
    public int getSize() {
        return this.stringList.size();
    }

    @Override
    public String getElementAt(int index) {
        return this.stringList.get(index);
    }

    public void addElement(String string) {
        this.stringList.add(string);
        this.fireIntervalAdded(this, this.stringList.size() - 1, this.stringList.size());
    }

    public void setContents(List<String> strings) {
        strings.addAll(strings);
    }

    public void clearContents() {
        this.stringList.clear();
    }
}
