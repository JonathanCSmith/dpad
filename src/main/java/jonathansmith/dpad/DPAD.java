package jonathansmith.dpad;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import io.netty.channel.local.LocalAddress;

import jonathansmith.dpad.api.common.util.Version;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.gui.GUIContainer;
import jonathansmith.dpad.common.gui.startup.StartupTabController;
import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.platform.Platform;
import jonathansmith.dpad.common.platform.PlatformConverter;

import jonathansmith.dpad.client.ClientEngine;

import jonathansmith.dpad.server.ServerEngine;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Main entry point for DPAD (Data Processing Analysis and Display)
 */
public class DPAD extends Thread {

    private static final int SOFTWARE_VERSION       = 1;
    private static final int SOFTWARE_MINOR_VERSION = 3;
    private static final int API_VERSION            = 1;
    private static final int DATABASE_VERSION       = 1;
    private static final int NETWORK_VERSION        = 1;

    private static boolean runtimeShutdownFlag = false;
    private static boolean errorFlag           = false;

    private static DPAD instance;
    // JCommander arguments. TODO: Detail these in args TODO: change to defaults
    @Parameter(names = {"-platform"}, converter = PlatformConverter.class, description = "Platform Type")
    private Platform platform         = null;
    @Parameter(names = {"-ip"}, description = "IP Address to connect to")
    private String   ipAddress        = "";
    @Parameter(names = {"-port"}, description = "Port to host or connect on")
    private int      port             = -1;
    @Parameter(names = {"-verbose"}, description = "Verbosity of logging")
    private boolean  isVerboseLogging = true;
    @Parameter(names = {"-debug"}, description = "Is debug runtime")
    private boolean  debugging        = false;
    private ServerEngine serverEngine  = null;
    private ClientEngine clientEngine  = null;
    private boolean      isInitialised = false;
    private boolean      isShutdown    = false;
    private GUIContainer  gui;
    private SocketAddress platformAddress;

    public DPAD() {
    }

    public static DPAD getInstance() {
        if (instance == null) {
            instance = new DPAD();
        }

        return instance;
    }

    /**
     * Main entry point for the DPAD program.
     * Parameters are pair values.
     * Parameters are as follows:
     *      property:                               specifier tag:          possible values:
     *          runtime type                            -platform               local, client, server
     *          target ip address                       -ip                     127.0.0.1 (i.e. a valid ip address)
     *          target port to host OR connect to       -port                   6567 (i.e. a valid port)
     *          logging verbosity                       -verbose                true or false
     *          is debug runtime                        -debug                  true or false
     *
     * @param args TODO
     */
    public static void main(String[] args) {
        DPAD main = DPAD.getInstance();

        // Inputs handling - JCommander is used to parse the input arguments.
        JCommander inputHandler = new JCommander(main);
        inputHandler.parse(args);

        // Check the address to see if it's valid - if not opt in to our config system
        if (main.getPlatformSelection() != null) {
            switch (main.getPlatformSelection()) {
                case LOCAL:
                    break;

                case CLIENT:
                    if (main.getIPAddress().contentEquals("")) {
                        main.handleError("Cannot run client from args without a valid ip address!", null, false);
                        main.setPlatformSelection(null);
                    }

                    else if (main.getPortAddress() == -1 || main.getPortAddress() < 0 || main.getPortAddress() > 65536) {
                        main.handleError("Cannot run client from args without a valid port!", null, false);
                        main.setPlatformSelection(null);
                    }
                    break;

                case SERVER:
                    if (main.getPortAddress() == -1 || main.getPortAddress() < 0 || main.getPortAddress() > 65536) {
                        main.handleError("Cannot run server from args without a valid port!", null, false);
                        main.setPlatformSelection(null);
                    }

                    break;
            }
        }

        // GUI Setup - has to be here so that all GUI components get the correct look
        // TODO: Reintroduce a cross platform look and feel
//        try {
//            UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
//        }
//
//        catch (ClassNotFoundException e) {
//            main.handleError("Look and feel not found", e, false);
//        }
//
//        catch (UnsupportedLookAndFeelException e) {
//            main.handleError("Unsupported look and feel!", e, false);
//        }
//
//        catch (InstantiationException e) {
//            main.handleError("Look and feel instantiate exception", e, false);
//        }
//
//        catch (IllegalAccessException e) {
//            main.handleError("Illegal access for look and feel", e, false);
//        }

        // Conditional setup dialog based on what arguments have been validated
        GUIContainer gui = new GUIContainer(main);
        if (main.getPlatformSelection() == null) {
            gui.init();
            StartupTabController tab = new StartupTabController();
            gui.addTab(tab);
            gui.run();

            // Sleep until we have acquired the startup parameters
            while (main.getPlatformSelection() == null && !runtimeShutdownFlag) {
                try {
                    Thread.sleep(100);
                }

                catch (InterruptedException ex) {
                    main.handleError("GUI was interrupted during setup procedure.", ex, true);
                }
            }

            if (runtimeShutdownFlag) {
                if (errorFlag) {
                    System.exit(-1);
                }

                else {
                    System.exit(0);
                }
            }

            else {
                gui.removeCoreTab(tab);
            }
        }

        else {
            if (main.getPlatformSelection() != Platform.LOCAL) {
                try {
                    main.setPlatformAddress(new InetSocketAddress(InetAddress.getByName(main.getIPAddress()), main.getPortAddress()));
                }

                catch (UnknownHostException ex) {
                    main.handleError("Could not bind ip address", ex, true);
                }
            }

            else {
                main.setPlatformAddress(new LocalAddress("6568"));
            }

            if (runtimeShutdownFlag) {
                if (errorFlag) {
                    System.exit(-1);
                }

                else {
                    System.exit(0);
                }
            }

            gui.init();
            gui.run();
        }

        main.init(gui);
        main.start();

        try {
            main.join();
        }

        catch (InterruptedException ex) {
            main.handleError("Outer thread was interrupted when joined to main thread.", ex, true);
        }

        while (!main.isShutdown) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                System.out.println("Could not wait for engines to shutdown... data integrity cannot be guaranteed!");
                errorFlag = true;
                break;
            }
        }

        if (errorFlag) {
            System.exit(-1);
        }

        else {
            System.exit(0);
        }
    }

    public void init(GUIContainer gui) {
        if (this.isInitialised) {
            return;
        }

        this.isInitialised = true;
        this.gui = gui;

        try {
            ConnectionState.registerPackets();
        }

        catch (Exception ex) {
            this.handleError("Could not register packets due to the exception: ", ex, true);
            return;
        }

        if (this.platform == Platform.LOCAL || this.platform == Platform.SERVER) {
            this.serverEngine = new ServerEngine(this.platformAddress);
        }

        if (this.platform == Platform.LOCAL || this.platform == Platform.CLIENT) {
            this.clientEngine = new ClientEngine(this.platformAddress);
        }
    }

    @Override
    public void run() {
        int engineCount = 0;
        if (this.getPlatformSelection() == Platform.SERVER || this.getPlatformSelection() == Platform.LOCAL) {
            this.serverEngine.injectVersion(new Version(SOFTWARE_VERSION, API_VERSION, DATABASE_VERSION, NETWORK_VERSION, SOFTWARE_MINOR_VERSION));
            this.serverEngine.start();
            engineCount++;

            while (!this.serverEngine.isSetup()) {
                try {
                    Thread.sleep(100);
                }

                catch (InterruptedException ex) {
                    this.handleError("Interrupted while waiting for server thread to finish startup", ex, true);
                    break;
                }
            }
        }

        if (this.getPlatformSelection() == Platform.CLIENT || this.getPlatformSelection() == Platform.LOCAL) {
            this.clientEngine.injectVersion(new Version(SOFTWARE_VERSION, API_VERSION, DATABASE_VERSION, NETWORK_VERSION, SOFTWARE_MINOR_VERSION));
            this.clientEngine.start();
            engineCount++;
        }

        int shutdownCount;
        while (!runtimeShutdownFlag) {
            shutdownCount = 0;
            if (this.serverEngine != null && this.serverEngine.isShuttingDown()) {
                shutdownCount++;
            }

            if (this.clientEngine != null && this.clientEngine.isShuttingDown()) {
                shutdownCount++;
            }

            // Both engines are shutdown, escape the while loop
            if (shutdownCount == engineCount) {
                runtimeShutdownFlag = true;
            }

            // Wait for the engines to be shutdown. This is a monitor thread.
            else {
                try {
                    Thread.sleep(100);
                }

                catch (InterruptedException ex) {
                    if (this.serverEngine != null) {
                        this.serverEngine.error("Background monitor thread was interrupted. A critical failure has occurred", ex);
                    }

                    if (this.clientEngine != null) {
                        this.clientEngine.error("Background monitor thread was interrupted. A critical failure has occurred", ex);
                    }

                    this.handleError("Background monitor thread was interrupted. A critical failure has occurred", ex, true);
                }
            }
        }

        /*
         * Handle an external shutdown call - here we need to shutdown everything as there is an application wide
         * error. However, because the root cause is unknown, we check the viability of each of the engines to determine
         * the shutdown type that should be performed.
         */
        if (this.serverEngine != null && !this.serverEngine.isShuttingDown()) {
            if (this.serverEngine.isViable()) {
                this.serverEngine.saveAndShutdown();
            }

            else {
                this.serverEngine.forceShutdown();
            }
        }

        if (this.clientEngine != null && !this.clientEngine.isShuttingDown()) {
            if (this.clientEngine.isViable()) {
                this.clientEngine.saveAndShutdown();
            }

            else {
                this.clientEngine.forceShutdown();
            }
        }

        this.isShutdown = true;
    }

    /**
     * Handle an error that propagates to all runtimes.
     *
     * @param errorHeader     the error information associated with a possible exception
     * @param ex              an optional exception for stack tracing
     * @param runtimeQuitFlag whether or not this error is fatal for all the runtimes
     */
    public void handleError(String errorHeader, Exception ex, boolean runtimeQuitFlag) {
        System.out.println(errorHeader);
        if (ex != null) {
            System.out.println(ex.fillInStackTrace());
        }

        errorFlag = true;
        runtimeShutdownFlag |= runtimeQuitFlag;
    }

    /**
     * Call to begin shutting down all the runtimes
     */
    public void shutdownAll() {
        runtimeShutdownFlag = true;
    }

    /**
     * Get the current running mode for this application instance.
     *
     * @return {@link jonathansmith.dpad.common.platform.Platform}
     */
    public Platform getPlatformSelection() {
        return this.platform;
    }

    /**
     * Set the running mode for this application instance. Does nothing once the application is running.
     *
     * @param platform {@link jonathansmith.dpad.common.platform.Platform}
     */
    public void setPlatformSelection(Platform platform) {
        if (this.isInitialised) {
            return;
        }

        this.platform = platform;
    }

    /**
     * Get a string representation of the application's ip address. Depending on the context, this is either the
     * ip address being hosted on, or connecting to. In a local context the ip is 127.0.0.1
     * @return
     */
    public String getIPAddress() {
        return this.ipAddress;
    }

    /**
     * Return an integer representation of the applications port address. Depending on the context this is either the
     * port being hosted on or connecting to.
     * @return
     */
    public int getPortAddress() {
        return this.port;
    }

    /**
     * Set the platform address. Note this will do nothing once the application is running fully.
     *
     * @param address
     */
    public void setPlatformAddress(SocketAddress address) {
        if (this.isInitialised) {
            return;
        }

        this.platformAddress = address;
    }

    /**
     * Return the core GUI Container for the current application.
     *
     * @return
     */
    public GUIContainer getGUI() {
        return this.gui;
    }

    /**
     * Return the engine based upon the platform.
     *
     * @param platform see {@link jonathansmith.dpad.common.platform.Platform}
     * @return the {@link jonathansmith.dpad.common.engine.Engine} for the Platform. It can be null if that portion of
     * the platform is not running.
     */
    public Engine getEngine(Platform platform) {
        switch (platform) {
            case SERVER:
                return this.serverEngine;

            case CLIENT:
                return this.clientEngine;

            default:
                return null;
        }
    }

    /**
     * Return whether the logging should be verbose.
     * @return
     */
    public boolean isVerboseLogging() {
        return this.isVerboseLogging;
    }

    /**
     * Return whether the software is running in debug mode.
     * @return
     */
    public boolean isDebugging() {
        return this.debugging;
    }
}
