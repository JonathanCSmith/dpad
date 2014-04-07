package jonathansmith.dpad.common.engine.util.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import jonathansmith.dpad.api.common.engine.util.log.ILogDisplay;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Utility class for live log displaying
 */
public class SwingAppender extends AppenderSkeleton {

    private final PatternLayout layout;
    private final ILogDisplay   target;

    public SwingAppender(PatternLayout layout, ILogDisplay target) {
        this.layout = layout;
        this.target = target;
    }

    @Override
    protected void append(LoggingEvent le) {
        this.target.appendLog(this.layout.format(le));
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return true;
    }
}
