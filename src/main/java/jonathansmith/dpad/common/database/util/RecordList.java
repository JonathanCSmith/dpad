package jonathansmith.dpad.common.database.util;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Implementation of {@link java.util.LinkedList} that allows contents to be evaluated based on hashcode
 */
public class RecordList<T> extends LinkedList<T> {

    /**
     * Check to see if the provided record is outdated compared to the one found within the records list (uses hashcode)
     *
     * @param newObject the most current record
     * @return 0 if not outdated, 1 if outdated, -1 if not found
     */
    public int isOutdated(Object newObject) {
        Iterator iter = this.descendingIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();

            // If it is the same record
            if (obj.hashCode() == newObject.hashCode()) {
                if (obj.equals(newObject)) {
                    return 0;
                }

                return 1;
            }
        }

        return -1;
    }

    @Override
    public boolean contains(Object o) {
        Iterator iter = this.descendingIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj.hashCode() == o.hashCode()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int indexOf(Object o) {
        Iterator iter = this.descendingIterator();
        int i = 0;
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj.hashCode() == o.hashCode()) {
                return i;
            }

            i++;
        }

        return -1;
    }
}
