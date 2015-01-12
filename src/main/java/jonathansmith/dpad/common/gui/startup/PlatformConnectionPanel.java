package jonathansmith.dpad.common.gui.startup;

import java.awt.event.*;

import javax.swing.*;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Acquires platform specific properties for runtime types
 */
public class PlatformConnectionPanel extends JDialog implements ActionListener, MouseListener, WindowListener {

    private static final String CLIENT_INFO = "Please enter the ip address of the server followed by a colon and the port of the server (i.e. 127.0.0.1:6868).";
    private static final String SERVER_INFO = "Please enter the port of the server. An ip address can also be specified by prefixing the port with the address followed by a colon (i.e. either 6868 (port) or 134.24.21.145:6868 (ipAddress:port).";

    private final StartupTabController controller;
    private final boolean              isHost;

    private JPanel     contentPane;
    private JButton    buttonOK;
    private JButton    buttonCancel;
    private JTextArea  info;
    private JTextField properties;

    public PlatformConnectionPanel(StartupTabController controller, boolean isHost) {
        this.controller = controller;
        this.isHost = isHost;
        this.setContentPane(this.contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonOK);

        this.initContent();

        this.buttonOK.addActionListener(this);
        this.buttonCancel.addActionListener(this);
        this.properties.addMouseListener(this);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
        this.contentPane.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initContent() {
        if (this.isHost) {
            this.info.setText(SERVER_INFO);
        }

        else {
            this.info.setText(CLIENT_INFO);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.properties.getText().contentEquals("Enter the required text here...")) {
            this.properties.setText("");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.buttonOK) {
            this.validateProperties(this.properties.getText());
            this.dispose();
        }

        else if (ae.getSource() == this.buttonCancel) {
            DPAD.getInstance().setPlatformSelection(null);
            this.dispose();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.validateProperties(this.properties.getText());
        this.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    private void validateProperties(String txt) {
        if (txt.contentEquals("Enter the required text here...") || txt.contentEquals("")) {
            DPAD.getInstance().handleError("IP Address || Port format exception", null, true);
            return;
        }

        String[] props = txt.split(":");
        if (!this.isHost && props.length != 2) {
            DPAD.getInstance().handleError("IP Address || Port format exception", null, true);
            return;
        }

        else if (props.length <= 0 || props.length > 2) {
            DPAD.getInstance().handleError("IP Address || Port format exception", null, true);
            return;
        }

        if (props.length == 2) {
            this.controller.setIPAddress(props[0]);
        }

        try {
            int portResult = Integer.parseInt(props[props.length - 1]);
            if (portResult < 0 || portResult > 65536) {
                return;
            }

            this.controller.setPort(portResult);
        }

        catch (NumberFormatException ex) {
            DPAD.getInstance().handleError("Number format exception in port address", ex, true);
        }
    }
}
