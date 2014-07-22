package jonathansmith.dpad.common.engine.executor;

import java.io.File;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.engine.io.FileSystem;
import jonathansmith.dpad.common.engine.util.configuration.Configuration;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Load or create a platform specific configuration.
 */
public class GlobalConfigurationLoadTask extends Task {

    private static final String TASK_NAME = "Configuration Load/Create";

    public GlobalConfigurationLoadTask(IEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        final File executionDomain = FileSystem.getExecutionDomain();
        Configuration.build(executionDomain, this.loggingEngine);
    }
}
