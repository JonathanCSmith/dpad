package jonathansmith.dpad.common.gui.startup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.swing.*;

import io.netty.channel.local.LocalAddress;

import jonathansmith.dpad.common.gui.ITabController;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Startup tab for DPAD
 */
public class StartupTabController implements ITabController, ActionListener {

    private static final String TITLE = "DPAD Startup";

    private JPanel     display;
    private JTextField headerText;
    private JTextField localInfo;
    private JTextField connectInfo;
    private JTextField hostInfo;
    private JButton    localButton;
    private JButton    serverButton;
    private JButton    clientButton;

    private String ipAddress = "";
    private int    port      = -1;
    private boolean isAlive;

    public StartupTabController() {
        this.localButton.addActionListener(this);
        this.clientButton.addActionListener(this);
        this.serverButton.addActionListener(this);
        this.isAlive = true;
    }

    public void setIPAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (!this.isAlive) {
            return;
        }

        Object source = ae.getSource();
        if (source == this.localButton) {
            DPAD.getInstance().setPlatformSelection(Platform.LOCAL);
            DPAD.getInstance().setPlatformAddress(new LocalAddress("6568"));
            return;
        }

        else if (source == this.serverButton) {
            PlatformConnectionDialog dialog = new PlatformConnectionDialog(this, true);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            DPAD.getInstance().setPlatformSelection(Platform.SERVER);
        }

        else if (source == this.clientButton) {
            PlatformConnectionDialog dialog = new PlatformConnectionDialog(this, false);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            DPAD.getInstance().setPlatformSelection(Platform.CLIENT);
        }

        else {
            return;
        }

        if (this.port == -1) {
            DPAD.getInstance().handleError("Invalid port for platform. DPAD failed to initialise.", null, true);
            return;
        }

        try {
            DPAD.getInstance().setPlatformAddress(new InetSocketAddress(InetAddress.getByName(this.ipAddress), this.port));
        }

        catch (UnknownHostException ex) {
            DPAD.getInstance().handleError("Invalid IP Address. DPAD failed to initialise", ex, true);
        }
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public JPanel getPanel() {
        return this.display;
    }

    @Override
    public void init() {
        this.display.setVisible(true);
    }

    @Override
    public void update() {

    }

    @Override
    public void onWindowClosing() {

    }

    @Override
    public void onWindowClosed() {

    }

    @Override
    public void shutdown(boolean force) {
        this.isAlive = false;
    }
}
