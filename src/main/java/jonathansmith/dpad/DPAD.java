package jonathansmith.dpad;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import io.netty.channel.local.LocalAddress;

import jonathansmith.dpad.api.APIAccess;
import jonathansmith.dpad.api.common.util.Version;

import jonathansmith.dpad.common.API;
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
    private static final int SOFTWARE_MINOR_VERSION = 0;
    private static final int API_VERSION            = 1;
    private static final int DATABASE_VERION        = 0;
    private static final int NETWORK_VERSION        = 0;

    private static boolean runtimeShutdownFlag = false;
    private static boolean errorFlag           = false;

    private static DPAD instance;
    private final LinkedList<Engine> engines = new LinkedList<Engine>();
    private ServerEngine serverEngine;
    private ClientEngine clientEngine;

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

    private boolean isInitialised = false;
    private boolean isShutdown    = false;

    private GUIContainer  gui;
    private SocketAddress platformAddress;

    public DPAD() {

    }

    /**
     * Main entry point for the DPAD program.
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
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }

        catch (ClassNotFoundException e) {
            main.handleError("Look and feel not found", e, false);
        }

        catch (UnsupportedLookAndFeelException e) {
            main.handleError("Unsupported look and feel!", e, false);
        }

        catch (InstantiationException e) {
            main.handleError("Look and feel instantiate exception", e, false);
        }

        catch (IllegalAccessException e) {
            main.handleError("Illegal access for look and feel", e, false);
        }

        // Conditional setup dialog based on what arguments have been validated
        GUIContainer gui = new GUIContainer(main);
        if (main.getPlatformSelection() == null) {
            gui.init();
            StartupTabController tab = new StartupTabController();
            gui.addTab(tab);
            gui.run();

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

    public static DPAD getInstance() {
        if (instance == null) {
            instance = new DPAD();
        }

        return instance;
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
        API api = new API();
        APIAccess.setAPI(api);
        api.setGUI(this.gui);
        // TODO: Add in api stuff

        if (this.getPlatformSelection() == Platform.SERVER || this.getPlatformSelection() == Platform.LOCAL) {
            this.serverEngine.injectVersion(new Version(SOFTWARE_VERSION, API_VERSION, DATABASE_VERION, NETWORK_VERSION, SOFTWARE_MINOR_VERSION));
            this.serverEngine.start();
            this.engines.add(this.serverEngine);

            while (!this.serverEngine.isSetup()) {
                try {
                    Thread.sleep(100);
                }

                catch (InterruptedException ex) {
                    this.handleError("Interrupted while waiting for server thread to finish startup", ex, true);
                    break;
                }
            }

            api.setServerEngine(this.serverEngine);
        }

        if (this.getPlatformSelection() == Platform.CLIENT || this.getPlatformSelection() == Platform.LOCAL) {
            this.clientEngine.injectVersion(new Version(SOFTWARE_VERSION, API_VERSION, DATABASE_VERION, NETWORK_VERSION, SOFTWARE_MINOR_VERSION));
            this.clientEngine.start();
            this.engines.add(this.clientEngine);
            api.setClientEngine(this.clientEngine);
        }

        api.finishAPISetup();

        while (!runtimeShutdownFlag) {
            int shutdownCount = 0;
            for (Engine engine : this.engines) {
                if (engine.isShuttingDown()) {
                    shutdownCount++;
                }
            }

            if (shutdownCount == this.engines.size()) {
                runtimeShutdownFlag = true;
            }

            else {
                try {
                    Thread.sleep(100);
                }

                catch (InterruptedException ex) {
                    for (Engine engine : this.engines) {
                        engine.error("Background monitor thread was interrupted. A critical failure has occurred", ex);
                    }

                    this.handleError("Background monitor thread was interrupted. A critical failure has occurred", ex, true);
                }
            }
        }

        for (Engine engine : this.engines) {
            // Theoretically any erroring engine would have shut itself down
            // TODO: Looks like the engines are never viable at this stage?
            if (engine.isViable()) {
                engine.handleShutdown("Exit on main thread.");
            }
        }

        this.isShutdown = true;
    }

    public Platform getPlatformSelection() {
        return this.platform;
    }

    public void setPlatformSelection(Platform platform) {
        this.platform = platform;
    }

    public void setPlatformAddress(SocketAddress address) {
        this.platformAddress = address;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public int getPortAddress() {
        return this.port;
    }

    public void handleError(String errorHeader, Exception ex, boolean runtimeQuitFlag) {
        System.out.println(errorHeader);
        if (ex != null) {
            System.out.println(ex.fillInStackTrace());
        }

        errorFlag = true;
        runtimeShutdownFlag |= runtimeQuitFlag;
    }

    public void shutdownAll() {
        runtimeShutdownFlag = true;
    }

    public boolean isVerboseLogging() {
        return this.isVerboseLogging;
    }

    public GUIContainer getGUI() {
        return this.gui;
    }

    public boolean isDebugging() {
        return this.debugging;
    }
}
