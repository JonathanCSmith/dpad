package jonathansmith.dpad.client.engine.executor.pluginselection;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.gui.display.Display;

/**
 * Created by Jon on 18/09/2014.
 * <p/>
 * Generic plugin selection executor
 */
public class PluginSelectionExecutor extends Executor {

    private static final String EXECUTOR_NAME = "Plugin Selection";

    private final PluginType type;
    private final Display    display;

    public PluginSelectionExecutor(Engine engine, PluginType type, Display display) {
        super(EXECUTOR_NAME + type.toString(), engine, false);

        this.type = type;
        this.display = display;
    }

    public enum PluginType {
        ANALYSING, LOADING
    }
}
