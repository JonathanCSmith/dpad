package jonathansmith.dpad.common.engine.io;

import java.io.File;
import java.net.URISyntaxException;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.platform.Platform;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Backbone filesystem for DPAD
 */
public class FileSystem {

    private final Engine engine;

    private File   rootDirectory;
    private File   platformDirectory;
    private String platformPath;

    public FileSystem(Engine engine) {
        this.engine = engine;
        this.init();
    }

    public void init() {
        try {
            String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File file = new File(classPath);
            if (classPath.contains(".jar")) {
                file = file.getParentFile();
            }

            this.rootDirectory = file;
            this.setupFileSystem();
        }

        catch (Exception ex) {
            this.engine.handleError("Could not instantiate FileSystem", ex);
        }
    }

    private void setupFileSystem() throws Exception {
        if (!this.rootDirectory.exists()) {
            throw new NullPointerException("Root directory does not exist");
        }

        File file = new File(this.rootDirectory, "DPAD");
        if (!file.exists() && !file.mkdir()) {
            throw new URISyntaxException("Could not build DPAD directory", file.getAbsolutePath());
        }

        File main = new File(file, this.engine.getPlatform().toString().toLowerCase());
        if (!main.exists() && !main.mkdir()) {
            throw new URISyntaxException("Could not create platform specific directory", main.getAbsolutePath());
        }

        this.platformDirectory = main;
        this.platformPath = main.getAbsolutePath();
        this.buildFileStructure();
    }

    private void buildFileStructure() throws URISyntaxException {
        boolean successful = true;
        if (successful && !this.getLogDirectory().exists()) {
            successful &= this.getLogDirectory().mkdir();
        }

        if (successful && this.engine.getPlatform() == Platform.SERVER && !this.getDatabaseDirectory().exists()) {
            successful &= this.getDatabaseDirectory().mkdir();
        }

        if (successful && !this.getPluginDirectory().exists()) {
            successful &= this.getPluginDirectory().mkdir();
        }

        if (successful && !this.getUpdateDirectory().exists()) {
            successful &= this.getUpdateDirectory().mkdir();
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
