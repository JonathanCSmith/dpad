package jonathansmith.dpad.common.engine;

import java.net.SocketAddress;

import org.slf4j.Logger;

import jonathansmith.dpad.api.common.engine.IEngine;
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
public abstract class Engine extends Thread implements IEngine {

    protected final EngineTabController engine_tab_controller;
    protected final SocketAddress       address;

    private final EventThread event_thread;

    private boolean  hasError            = false;
    private boolean  shutdownFlag        = false;
    private boolean  isSetup             = false;
    private boolean  fileSystemSetup     = false;
    private boolean  networkManagerSetup = false;
    private boolean  pluginManagerSetup  = false;
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

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        if (this.logger != null) {
            this.error("Cannot change loggers once they are set!", null);
            return;
        }

        this.logger = logger;
    }

    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        if (fileSystem == null || this.fileSystemSetup) {
            this.error("Cannot change the filesystem once it has been set!", null);
            return;
        }

        this.fileSystem = fileSystem;
        this.fileSystemSetup = true;

        this.setPluginManager(new PluginManager(fileSystem.getPluginDirectory().getAbsolutePath(), fileSystem.getUpdateDirectory().getAbsolutePath(), this));
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public void setPluginManager(PluginManager pluginManagerThread) {
        if (pluginManagerThread == null || this.pluginManagerSetup) {
            this.error("Cannot change the plugin manager once it has been set!", null);
            return;
        }

        this.pluginManager = pluginManagerThread;
        this.pluginManagerSetup = true;
    }

    protected NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        if (networkManager == null || this.networkManagerSetup) {
            this.error("Cannot change the network manager once it has been set!", null);
            return;
        }

        this.networkManager = networkManager;
        this.networkManagerSetup = true;
    }

    public IExecutor getCurrentExecutor() {
        return this.currentExecutor;
    }

    private void setCurrentExecutor(Executor op) {
        this.currentExecutor = op;
    }

    private Executor getProposedExecutor() {
        return this.proposedExecutor;
    }

    public void setProposedExecutorWithoutWaiting(Executor op) {
        if (this.proposedExecutor != null) {
            this.error("An executor has been overridden, this is a program error that is likely due to concurrency issues...", null);
        }

        this.proposedExecutor = op;
    }

    public void setAndWaitForProposedExecutor(Executor op) {
        this.setProposedExecutorWithoutWaiting(op);
//        while (!op.isExecuting() && !op.hasFinished()) {
//            try {
//                Thread.sleep(5);
//            }
//
//            catch (InterruptedException ex) {
//
//            }
//        }
    }

    public boolean hasErrored() {
        return this.hasError;
    }

    public boolean isShuttingDown() {
        return this.shutdownFlag;
    }

    @Override
    public boolean isSetup() {
        return this.isSetup;
    }

    public void setSetupFinished() {
        this.isSetup = true;
    }

    @Override
    public Version getVersion() {
        return this.version;
    }

    @Override
    public IEventThread getEventThread() {
        return this.event_thread;
    }

    @Override
    public synchronized void trace(String message, Throwable e) {
        Logger logger = this.getLogger();
        if (logger != null) {
            if (e != null) {
                logger.trace(message, e);
            }

            else {
                logger.trace(message);
            }
        }
    }

    @Override
    public synchronized void debug(String message, Throwable e) {
        Logger logger = this.getLogger();
        if (logger != null) {
            if (e != null) {
                logger.debug(message, e);
            }

            else {
                logger.debug(message);
            }
        }
    }

    @Override
    public synchronized void info(String message, Throwable e) {
        Logger logger = this.getLogger();
        if (logger != null) {
            if (e != null) {
                logger.info(message, e);
            }

            else {
                logger.info(message);
            }
        }
    }

    @Override
    public synchronized void warn(String message, Throwable e) {
        Logger logger = this.getLogger();
        if (logger != null) {
            if (e != null) {
                logger.warn(message, e);
            }

            else {
                logger.warn(message);
            }
        }
    }

    @Override
    public synchronized void error(String message, Throwable e) {
        Logger logger = this.getLogger();
        if (logger != null) {
            if (e != null) {
                logger.error(message, e);
            }

            else {
                logger.error(message);
            }
        }
    }

    @Override
    public synchronized void handleError(String message, Throwable e) {
        this.error(message, e);
        this.hasError = true;
    }

    @Override
    public synchronized void handleShutdown(String exitMessage) {
        this.debug(exitMessage, null);
        this.shutdownFlag = true;
    }
}
