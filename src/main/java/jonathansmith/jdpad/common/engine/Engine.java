package jonathansmith.jdpad.common.engine;

import java.net.SocketAddress;

import org.slf4j.Logger;

import jonathansmith.jdpad.api.common.engine.IEngine;
import jonathansmith.jdpad.api.common.engine.event.IEventThread;
import jonathansmith.jdpad.api.common.engine.util.log.ILogDisplay;

import jonathansmith.jdpad.common.engine.event.EventThread;
import jonathansmith.jdpad.common.engine.executor.Executor;
import jonathansmith.jdpad.common.engine.io.FileSystem;
import jonathansmith.jdpad.common.gui.EngineTabController;
import jonathansmith.jdpad.common.network.NetworkManager;
import jonathansmith.jdpad.common.platform.Platform;

import jonathansmith.jdpad.server.engine.executor.ServerIdle;

import jonathansmith.jdpad.JDPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Parent class for all runnable engines in the JDPAD framework
 */
public abstract class Engine extends Thread implements IEngine {

    protected final SocketAddress address;
    protected final EventThread   eventThread;

    protected FileSystem fileSystem = null;
    protected Logger     logger     = null;

    protected EngineTabController tabDisplay;
    protected Executor            currentExecutor;

    private boolean hasErrored          = false;
    private boolean shutdownFlag        = false;
    private boolean networkManagerSetup = false;

    private NetworkManager networkManager;

    public Engine(SocketAddress address, EngineTabController tabController) {
        this.address = address;
        this.tabDisplay = tabController;
        this.eventThread = new EventThread(this);
    }

    public abstract Platform getPlatform();

    public void init() {
        this.eventThread.start();
        // TODO: Plugin manager start
    }

    @Override
    public void run() {
        while (!this.hasErrored() && !this.isShuttingDown()) {
            Executor currentExecutor = this.getCurrentExecutor();
            if (currentExecutor.hasFinished()) {
                this.setCurrentExecutor(new ServerIdle(this));
            }

            else {
                currentExecutor.execute();
            }
        }

        if (this.hasErrored()) {
            this.forceShutdown();
        }

        else if (this.isShuttingDown()) {
            this.saveAndShutdown();
        }
    }

    @Override
    public boolean isViable() {
        return this.hasErrored();
    }

    public void saveAndShutdown() {
        if (this.tabDisplay != null) {
            this.tabDisplay.shutdown(false);
            JDPAD.getInstance().getGUI().removeTab(this.tabDisplay);
            this.tabDisplay = null;
        }

        if (this.getCurrentExecutor() != null) {
            this.getCurrentExecutor().shutdown(false);
        }

        if (this.networkManager != null) {
            this.networkManager.shutdown(false);
        }

        if (this.getEventThread() != null) {
            this.getEventThread().shutdown(false);
        }
    }

    public void forceShutdown() {
        if (this.tabDisplay != null) {
            this.tabDisplay.shutdown(true);
            JDPAD.getInstance().getGUI().removeTab(this.tabDisplay);
            this.tabDisplay = null;
        }

        if (this.getCurrentExecutor() != null) {
            this.getCurrentExecutor().shutdown(true);
        }

        if (this.networkManager != null) {
            this.networkManager.shutdown(true);
        }

        if (this.getEventThread() != null) {
            this.getEventThread().shutdown(true);
        }
    }

    public ILogDisplay getDisplayTab() {
        return this.tabDisplay;
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
        if (this.fileSystem != null) {
            this.error("Cannot change the filesystem once it has been set!", null);
            return;
        }

        this.fileSystem = fileSystem;
    }

    protected NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        if (networkManager == null || this.networkManagerSetup) {
            return;
        }

        this.networkManager = networkManager;
        this.networkManagerSetup = true;
    }

    @Override
    public void setCurrentExecutor(Executor op) {
        this.currentExecutor = op;
    }

    @Override
    public Executor getCurrentExecutor() {
        return this.currentExecutor;
    }

    @Override
    public boolean hasErrored() {
        return this.hasErrored;
    }

    @Override
    public boolean isShuttingDown() {
        return this.shutdownFlag;
    }

    @Override
    public IEventThread getEventThread() {
        return this.eventThread;
    }

    @Override
    public void startShutdown() {
        this.shutdownFlag = true;
    }

    @Override
    public void trace(String message, Throwable e) {
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
    public void debug(String message, Throwable e) {
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
    public void info(String message, Throwable e) {
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
    public void warn(String message, Throwable e) {
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
    public void error(String message, Throwable e) {
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
    public void handleError(String message, Throwable e, boolean shutdownThreadFlag) {
        this.error(message, e);
        if (shutdownThreadFlag) {
            this.hasErrored = true;
        }
    }

    public abstract String getVersion();

    public String getAddress() {
        return this.address.toString().split(":")[0];
    }

    public String getPort() {
        return this.address.toString().split(":")[1];
    }
}
