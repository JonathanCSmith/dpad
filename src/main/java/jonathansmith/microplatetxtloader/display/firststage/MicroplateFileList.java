package jonathansmith.microplatetxtloader.display.firststage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * Created by Jon on 29/09/2014.
 */
public class MicroplateFileList extends AbstractListModel {

    private final ArrayList<File> list = new ArrayList<File>();

    @Override
    public int getSize() {
        return this.list.size();
    }

    @Override
    public String getElementAt(int index) {
        return this.list.get(index).getAbsolutePath();
    }

    public void addElementAt(File file) {
        this.list.add(file);
        this.fireIntervalAdded(this, this.list.size() - 1, this.list.size());
    }

    public void updateFiles(List<File> files) {
        this.list.clear();
        this.list.addAll(files);
        this.fireContentsChanged(this.list, 0, this.getSize());
    }
}
