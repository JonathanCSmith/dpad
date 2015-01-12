package jonathansmith.dpad.server.engine.executor.startup;

import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;

import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecord;

/**
 * Created by Jon on 17/07/2014.
 * <p/>
 * Create or load the background server configuration
 */
public class LoadOrCreateServerConfiguration extends Task {

    private static final String TASK_NAME = "Server Configuration Setup";

    private final GatherServerStartupPropertiesTask configTask;

    public LoadOrCreateServerConfiguration(ServerEngine engine, GatherServerStartupPropertiesTask configTask) {
        super(TASK_NAME, engine);

        this.configTask = configTask;
    }

    @Override
    protected void runTask() {
        this.loggingEngine.trace("Beginning server configuration setup", null);
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 1, 0));

        ServerConfigurationRecord configuration = ((ServerEngine) this.loggingEngine).getServerConfiguration();

        if (configuration == null) {
            this.loggingEngine.trace("Creating new configuration row", null);
            configuration = new ServerConfigurationRecord();
            configuration.setUUID(((ServerEngine) this.loggingEngine).getServerUUID().toString());
            configuration.setUserVerificationRequired(this.configTask.getServerSetupConfiguration().isAutoVerificationEnabled());
            ((ServerEngine) this.loggingEngine).saveServerConfiguration(configuration);
        }

        this.loggingEngine.trace("Configuration presence verified", null);
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 1, 1));
    }
}
