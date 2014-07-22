package jonathansmith.dpad.common.engine.io;

import java.io.File;
import java.net.URISyntaxException;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.util.configuration.Configuration;
import jonathansmith.dpad.common.engine.util.configuration.ConfigurationProperty;
import jonathansmith.dpad.common.engine.util.configuration.FileConfigurationValue;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Backbone filesystem for DPAD
 */
public class FileSystem {

    private static final File execution_domain;

    static {
        String path;
        File file;

        try {
            path = FileSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            file = new File(path);

            if (path.contains(".jar")) {
                file = file.getParentFile();
            }
        }

        catch (URISyntaxException ex) {
            DPAD.getInstance().handleError("Cannot identify execution path", ex, true);
            path = "";

            file = new File(path);
        }

        execution_domain = file;
    }

    private final Engine engine;
    private       File   dataDirectory;
    private       File   platformDirectory;

    public FileSystem(Engine engine) {
        this.engine = engine;

        try {
            this.dataDirectory = ((FileConfigurationValue) Configuration.getInstance().getConfigValue(ConfigurationProperty.LAST_KNOWN_DATA_LOCATION)).getPropertyValue();
            this.setupFileSystem();
        }

        catch (Exception ex) {
            this.engine.handleError("Could not setup filesystem", ex);
        }
    }

    public static File getExecutionDomain() {
        return execution_domain;
    }

    private void setupFileSystem() throws Exception {
        if (!this.dataDirectory.exists()) {
            throw new NullPointerException("Root directory does not exist");
        }

        File file = new File(this.dataDirectory, "DPAD");
        if (!file.exists() && !file.mkdir()) {
            throw new URISyntaxException("Could not build DPAD directory", file.getAbsolutePath());
        }

        File main = new File(file, this.engine.getPlatform().toString().toLowerCase());
        if (!main.exists() && !main.mkdir()) {
            throw new URISyntaxException("Could not create platform specific directory", main.getAbsolutePath());
        }

        this.platformDirectory = main;
        this.buildFileStructure();
    }

    private void buildFileStructure() throws URISyntaxException {
        boolean successful = true;
        if (!this.getLogDirectory().exists()) {
            successful = this.getLogDirectory().mkdir();
        }

        if (successful && this.engine.getPlatform() == Platform.SERVER && !this.getDatabaseDirectory().exists()) {
            successful = this.getDatabaseDirectory().mkdir();
        }

        if (successful && !this.getPluginDirectory().exists()) {
            successful = this.getPluginDirectory().mkdir();
        }

        if (successful && !this.getUpdateDirectory().exists()) {
            successful = this.getUpdateDirectory().mkdir();
        }

        if (!successful) {
            throw new URISyntaxException("Could not build file structure", "ALL");
        }
    }

    public File getLogDirectory() {
        return new File(this.platformDirectory, "Logs");
    }

    public File getDatabaseDirectory() {
        if (this.engine.getPlatform() != Platform.SERVER) {
            return null;
        }

        return new File(this.platformDirectory, "Database");
    }

    public File getUpdateDirectory() {
        return new File(this.platformDirectory, "Updates");
    }

    public File getPluginDirectory() {
        return new File(this.platformDirectory, "Plugins");
    }
}
