package jonathansmith.dpad.common.engine.util.log;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Represents a target for live logging display. Used in custom GUIs to receive logging events
 */
public interface ILogDisplay {

    /**
     * Method used to append information into the provided Log display. Used internally to generify log displays
     *
     * @param format the string to append
     */
    void appendLog(String format);
}
