package jonathansmith.dpad.common.engine;

import java.net.SocketAddress;

import org.slf4j.Logger;

import jonathansmith.dpad.api.common.engine.executor.IExecutor;
import jonathansmith.dpad.api.common.util.Version;
import jonathansmith.dpad.api.plugins.events.IEventThread;

import jonathansmith.dpad.common.engine.event.EventThread;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.engine.io.FileSystem;
import jonathansmith.dpad.common.engine.util.log.ILogDisplay;
import jonathansmith.dpad.common.gui.EngineTabController;
import jonathansmith.dpad.common.network.NetworkManager;
import jonathansmith.dpad.common.platform.Platform;
import jonathansmith.dpad.common.plugin.PluginManager;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Parent class for all runnable engines in the DPAD framework
 */
public abstract class Engine extends Thread {

    protected final EngineTabController engine_tab_controller;
    protected final SocketAddress       address;

    private final EventThread event_thread;

    private boolean  hasError            = false;
    private boolean  shutdownFlag        = false;
    private boolean  isSetup             = false;
    private Executor currentExecutor     = null;
    private Executor proposedExecutor    = null;

    private Logger         logger;
    private FileSystem     fileSystem;
    private NetworkManager networkManager;
    private PluginManager  pluginManager;
    private Version        version;

    public Engine(SocketAddress address, EngineTabController tabController) {
        this.address = address;
        this.engine_tab_controller = tabController;
        this.event_thread = new EventThread(this);
    }

    public abstract Platform getPlatform();

    @Override
    public void run() {
        while (!this.hasErrored() && !this.isShuttingDown()) {
            if (this.currentExecutor == null) {
                if (this.getProposedExecutor() == null) {
                    this.currentExecutor = this.getDefaultExecutor();
                }

                else {
                    this.currentExecutor = this.getProposedExecutor();
                    this.proposedExecutor = null;
                }
            }

            if (this.currentExecutor.hasFinished()) {
                this.currentExecutor = null;
            }

            else if (this.currentExecutor.isRepeatExecution() && this.getProposedExecutor() != null) {
                this.currentExecutor = null;
            }

            if (this.currentExecutor != null && !this.currentExecutor.isExecuting()) {
                this.currentExecutor.execute();
            }

            try {
                Thread.sleep(5);
            }

            catch (InterruptedException ex) {
                // REVIST
            }
        }

        if (this.hasErrored()) {
            this.forceShutdown();
        }

        else {
            this.saveAndShutdown();
        }
    }

    protected abstract Executor getDefaultExecutor();

    public boolean isViable() {
        return this.hasErrored();
    }

    public void saveAndShutdown() {
        if (this.getCurrentExecutor() != null) {
            this.getCurrentExecutor().shutdown(false);
        }

        this.event_thread.shutdown(true);

        if (this.engine_tab_controller != null) {
            this.engine_tab_controller.shutdown(false);
            DPAD.getInstance().getGUI().removeCoreTab(this.engine_tab_controller);
        }

        if (this.networkManager != null) {
            this.networkManager.shutdown(false);
        }

        if (this.pluginManager != null) {
            this.pluginManager.shutdown(false);
        }
    }

    public void forceShutdown() {
        if (this.getCurrentExecutor() != null) {
            this.getCurrentExecutor().shutdown(true);
        }

        this.event_thread.shutdown(true);

        if (this.engine_tab_controller != null) {
            this.engine_tab_controller.shutdown(true);
            DPAD.getInstance().getGUI().removeCoreTab(this.engine_tab_controller);
        }

        if (this.networkManager != null) {
            this.networkManager.shutdown(true);
        }

        if (this.pluginManager != null) {
            this.pluginManager.shutdown(true);
        }
    }

    public void injectVersion(Version version) {
        this.version = version;
    }

    public ILogDisplay getDisplayTab() {
        return this.engine_tab_controller;
    }

    /**
     * Set the logger for the engine
     *
     * @param logger
     */
    public void setLogger(Logger logger) {
        if (this.logger != null) {
            this.error("Cannot change loggers once they are set!", null);
            return;
        }

        this.logger = logger;
    }

    /**
     * Get the current filesystem for the engine. Can be null
     *
     * @return
     */
    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    /**
     * Set the current filesystem for the engine.
     *
     * @param fileSystem
     */
    public void setFileSystem(FileSystem fileSystem) {
        if (fileSystem == null || this.fileSystem != null) {
            this.error("Cannot change the filesystem once it has been set!", null);
            return;
        }

        this.fileSystem = fileSystem;

        this.setPluginManager(new PluginManager(fileSystem.getPluginDirectory().getAbsolutePath(), fileSystem.getUpdateDirectory().getAbsolutePath(), this));
    }

    /**
     * Get the current plugin manager. Can be null
     *
     * @return
     */
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    /**
     * Set the current plugin manager.
     *
     * @param pluginManagerThread
     */
    public void setPluginManager(PluginManager pluginManagerThread) {
        if (pluginManagerThread == null || this.pluginManager != null) {
            this.error("Cannot change the plugin manager once it has been set!", null);
            return;
        }

        this.pluginManager = pluginManagerThread;
    }

    /**
     * Return the current network manager. Can be null
     *
     * @return
     */
    protected NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    /**
     * Set the current network manager. Cannot be changed once it has been established
     *
     * @param networkManager
     */
    public void setNetworkManager(NetworkManager networkManager) {
        if (networkManager == null || this.networkManager != null) {
            this.error("Cannot change the network manager once it has been set!", null);
            return;
        }

        this.networkManager = networkManager;
    }

    /**
     * Return the current executor
     *
     * @return
     */
    public IExecutor getCurrentExecutor() {
        return this.currentExecutor;
    }

    /**
     * Return the current proposed executor. Can be null!
     *
     * @return
     */
    private Executor getProposedExecutor() {
        return this.proposedExecutor;
    }

    /**
     * Set a new executor for the engine. It will override and existing executors waiting in the wings.
     *
     * @param op
     */
    public void setProposedExecutor(Executor op) {
        if (this.proposedExecutor != null) {
            this.error("An executor has been overridden, this is a program error that is likely due to concurrency issues...", null);
        }

        this.proposedExecutor = op;
    }

    /**
     * Return whether or not the engine has an error
     *
     * @return
     */
    public boolean hasErrored() {
        return this.hasError;
    }

    /**
     * Return whether or not the engine is currently shutting down or is shut down
     *
     * @return
     */
    public boolean isShuttingDown() {
        return this.shutdownFlag;
    }

    /**
     * Return whether or not the IEngine has been setup. Until this returns true behaviour cannot be guaranteed.
     *
     * @return is this setup
     */
    public boolean isSetup() {
        return this.isSetup;
    }

    /**
     * Set whether the engine is setup. Cannot be changed once the engine has been setup once
     */
    public void setSetupFinished() {
        if (this.isSetup) {
            return;
        }

        this.isSetup = true;
    }

    /**
     * Return the version of this engine. Includes the network protocol version string for comparing client / server compatibility
     *
     * @return String version information
     */
    public Version getVersion() {
        return this.version;
    }

    /**
     * Return the event thread for the engine
     *
     * @return {@link jonathansmith.dpad.api.plugins.events.IEventThread} the event thread used by the current engine
     */
    public IEventThread getEventThread() {
        return this.event_thread;
    }

    /**
     * Log a trace with a possible exception
     *
     * @param message
     * @param e
     */
    public synchronized void trace(String message, Throwable e) {
        Logger logger = this.logger;
        if (logger != null) {
            if (e != null) {
                logger.trace(message, e);
            }

            else {
                logger.trace(message);
            }
        }
    }

    /**
     * Log debug information with a possible exception
     *
     * @param message
     * @param e
     */
    public synchronized void debug(String message, Throwable e) {
        Logger logger = this.logger;
        if (logger != null) {
            if (e != null) {
                logger.debug(message, e);
            }

            else {
                logger.debug(message);
            }
        }
    }

    /**
     * Log info with a possible exception
     *
     * @param message
     * @param e
     */
    public synchronized void info(String message, Throwable e) {
        Logger logger = this.logger;
        if (logger != null) {
            if (e != null) {
                logger.info(message, e);
            }

            else {
                logger.info(message);
            }
        }
    }

    /**
     * Log a warning with a possible exception
     *
     * @param message
     * @param e
     */
    public synchronized void warn(String message, Throwable e) {
        Logger logger = this.logger;
        if (logger != null) {
            if (e != null) {
                logger.warn(message, e);
            }

            else {
                logger.warn(message);
            }
        }
    }

    /**
     * Log an error with a possible exception
     *
     * @param message
     * @param e
     */
    public synchronized void error(String message, Throwable e) {
        Logger logger = this.logger;
        if (logger != null) {
            if (e != null) {
                logger.error(message, e);
            }

            else {
                logger.error(message);
            }
        }
    }

    /**
     * Handle an error that will cause this engine to shutdown
     *
     * @param message
     * @param e
     */
    public synchronized void handleError(String message, Throwable e) {
        this.error(message, e);
        this.hasError = true;
    }

    /**
     * Causes a shutdown of the engine with full saving.
     *
     * @param exitMessage debug shutdown message
     */
    public synchronized void handleShutdown(String exitMessage) {
        this.debug(exitMessage, null);
        this.shutdownFlag = true;
    }
}
