package jonathansmith.dpad.common.engine;

import java.net.SocketAddress;

import org.slf4j.Logger;

import jonathansmith.dpad.DPAD;
import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.common.engine.event.IEventThread;
import jonathansmith.dpad.common.engine.event.EventThread;
import jonathansmith.dpad.common.engine.executor.Executor;
import jonathansmith.dpad.common.engine.executor.IdleExecutor;
import jonathansmith.dpad.common.engine.io.FileSystem;
import jonathansmith.dpad.common.engine.util.log.ILogDisplay;
import jonathansmith.dpad.common.gui.EngineTabController;
import jonathansmith.dpad.common.network.NetworkManager;
import jonathansmith.dpad.common.platform.Platform;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Parent class for all runnable engines in the DPAD framework
 */
public abstract class Engine extends Thread implements IEngine {

    protected final SocketAddress address;
    protected final EventThread   eventThread;

    protected FileSystem fileSystem = null;
    protected Logger     logger     = null;

    protected EngineTabController tabDisplay;

    private boolean  hasError            = false;
    private boolean  shutdownFlag        = false;
    private boolean  networkManagerSetup = false;
    private Executor currentExecutor     = null;
    private Executor proposedExecutor    = null;

    private NetworkManager networkManager;
    private String         version;

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
            if (!(currentExecutor instanceof IdleExecutor)) {
                if (currentExecutor.hasFinished()) {

                    Executor proposedExecutor = this.getProposedExecutor();
                    if (proposedExecutor == null) {
                        this.setCurrentExecutor(new IdleExecutor(this));
                    }

                    else {
                        this.setCurrentExecutor(proposedExecutor);
                    }
                }

                else if (!currentExecutor.isExecuting()) {
                    currentExecutor.execute();
                }
            }

            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                // TODO: Log?!
            }
        }

        if (this.hasErrored()) {
            this.forceShutdown();
        }

        else {
            this.saveAndShutdown();
        }
    }

    public boolean isViable() {
        return this.hasErrored();
    }

    public void saveAndShutdown() {
        if (this.getCurrentExecutor() != null) {
            this.getCurrentExecutor().shutdown(false);
        }

        this.eventThread.shutdown(true);

        if (this.tabDisplay != null) {
            this.tabDisplay.shutdown(false);
            DPAD.getInstance().getGUI().removeCoreTab(this.tabDisplay);
            this.tabDisplay = null;
        }

        if (this.networkManager != null) {
            this.networkManager.shutdown(false);
        }
    }

    public void forceShutdown() {
        if (this.getCurrentExecutor() != null) {
            this.getCurrentExecutor().shutdown(true);
        }

        this.eventThread.shutdown(true);

        if (this.tabDisplay != null) {
            this.tabDisplay.shutdown(true);
            DPAD.getInstance().getGUI().removeCoreTab(this.tabDisplay);
            this.tabDisplay = null;
        }

        if (this.networkManager != null) {
            this.networkManager.shutdown(true);
        }
    }

    public String getVersion() {
        return this.version;
    }

    public void injectVersion(String version) {
        this.version = version;
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

    public void setNetworkManager(NetworkManager networkManager) {
        if (networkManager == null || this.networkManagerSetup) {
            return;
        }

        this.networkManager = networkManager;
        this.networkManagerSetup = true;
    }

    public Executor getCurrentExecutor() {
        return this.currentExecutor;
    }

    private void setCurrentExecutor(Executor op) {
        this.currentExecutor = op;
    }

    private Executor getProposedExecutor() {
        return this.proposedExecutor;
    }

    public void setProposedExecutor(Executor op) {
        this.proposedExecutor = op;
    }

    public boolean hasErrored() {
        return this.hasError;
    }

    public boolean isShuttingDown() {
        return this.shutdownFlag;
    }

    @Override
    public IEventThread getEventThread() {
        return this.eventThread;
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
    public synchronized void handleError(String message, Throwable e, boolean shutdownThreadFlag) {
        this.error(message, e);
        if (shutdownThreadFlag) {
            this.hasError = true;
        }
    }

    @Override
    public synchronized void handleShutdown(String exitMessage) {
        this.debug(exitMessage, null);
        this.shutdownFlag = true;
    }
}
