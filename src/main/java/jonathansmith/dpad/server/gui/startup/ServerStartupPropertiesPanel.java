package jonathansmith.dpad.server.gui.startup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.*;

import jonathansmith.dpad.api.plugins.display.DisplayPanel;

import jonathansmith.dpad.common.engine.util.configuration.Configuration;
import jonathansmith.dpad.common.engine.util.configuration.ConfigurationProperty;
import jonathansmith.dpad.common.engine.util.configuration.FileConfigurationValue;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.engine.event.ServerStartupPropertiesFinishEvent;
import jonathansmith.dpad.server.engine.util.config.ServerStartupProperties;

/**
 * Created by Jon on 13/07/2014.
 * <p/>
 * Server configuration display panel.
 */
public class ServerStartupPropertiesPanel extends DisplayPanel implements ActionListener {

    private final ServerEngine  engine;
    private final Configuration configuration;

    private File dataFile = null;

    private boolean isNewServer = false;

    private JRadioButton   radioButton1;
    private JRadioButton   radioButton2;
    private JPanel         contentPane;
    private JTextField     pleaseEnterTheAppropriateTextField;
    private JTextField     superUserText;
    private JTextField     username;
    private JTextField     pwdText;
    private JButton        finishButton;
    private JPasswordField pwd;
    private JPasswordField pwdConfirm;
    private JTextField     pwdConfirmText;
    private JCheckBox      requireUsersToBeCheckBox;
    private JButton        setDPADPathButton;
    private JTextField     dataPath;
    private JTextField     path;
    private ButtonGroup    buttonGroup1;

    public ServerStartupPropertiesPanel(ServerEngine engine) {
        this.engine = engine;
        this.configuration = Configuration.getInstance();

        this.radioButton1.addActionListener(this);
        this.radioButton2.addActionListener(this);
        this.setDPADPathButton.addActionListener(this);
        this.finishButton.addActionListener(this);

        this.dataFile = ((FileConfigurationValue) this.configuration.getConfigValue(ConfigurationProperty.LAST_KNOWN_DATA_LOCATION)).getPropertyValue();
        this.dataPath.setText(this.dataFile.getAbsolutePath());

        this.path.setVisible(false);
        this.dataPath.setVisible(false);
        this.setDPADPathButton.setVisible(false);
        this.finishButton.setVisible(false);
        this.pleaseEnterTheAppropriateTextField.setVisible(false);
        this.superUserText.setVisible(false);
        this.username.setVisible(false);
        this.pwdText.setVisible(false);
        this.pwd.setVisible(false);
        this.pwdConfirmText.setVisible(false);
        this.pwdConfirm.setVisible(false);
        this.requireUsersToBeCheckBox.setVisible(false);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.radioButton1) {
            this.radioSwitch(true);
        }

        else if (ae.getSource() == this.radioButton2) {
            this.radioSwitch(false);
        }

        else if (ae.getSource() == this.finishButton) {
            if (this.buttonGroup1.getSelection() != null) {
                if (this.username.getText().contentEquals("")) {
                    this.showModal("Missing Username!");
                    return;
                }

                try {
                    char[] pwd1 = this.pwd.getPassword();
                    char[] pwd2 = this.pwd.getPassword();

                    if (pwd1.length == 0 || pwd2.length == 0) {
                        this.showModal("Passwords cannot be null!");
                    }

                    if (!Arrays.equals(pwd1, pwd2)) {
                        this.showModal("Passwords do not match");
                        this.pwd.setText("");
                        this.pwdConfirm.setText("");
                        return;
                    }
                }

                catch (NullPointerException ex) {
                    this.showModal("One of your password fields was empty!");
                    return;
                }

                if (this.dataFile != ((FileConfigurationValue) this.configuration.getConfigValue(ConfigurationProperty.LAST_KNOWN_DATA_LOCATION)).getPropertyValue()) {
                    this.configuration.setConfigurationValue(ConfigurationProperty.LAST_KNOWN_DATA_LOCATION, new FileConfigurationValue(this.dataFile));
                    this.configuration.save(this.engine);
                }

                ServerStartupProperties configuration = new ServerStartupProperties(this.isNewServer, this.username.getText(), new String(this.pwd.getPassword()), false /* TODO: this.buttonGroup1.isSelected(this.radioButton1.getModel()) && this.requireUsersToBeCheckBox.isSelected() */);
                this.engine.getEventThread().postEvent(new ServerStartupPropertiesFinishEvent(configuration));
            }
        }

        else if (ae.getSource() == this.setDPADPathButton) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = chooser.showOpenDialog(this.contentPane);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                this.dataFile = chooser.getSelectedFile();
                this.dataPath.setText(this.dataFile.getAbsolutePath());
            }
        }
    }

    private void radioSwitch(boolean state) {
        this.isNewServer = state;
        this.pleaseEnterTheAppropriateTextField.setVisible(true);
        this.superUserText.setVisible(true);
        this.username.setText("");
        this.username.setVisible(true);
        this.pwdText.setVisible(true);
        this.pwd.setText("");
        this.pwd.setVisible(true);
        this.pwdConfirmText.setVisible(state);
        this.pwdConfirm.setText("");
        this.pwdConfirm.setVisible(state);
        /* TODO:
        this.requireUsersToBeCheckBox.setVisible(state);
        */
        this.contentPane.validate();

        this.path.setVisible(true);
        this.dataPath.setVisible(true);
        this.setDPADPathButton.setVisible(true);
        this.finishButton.setVisible(true);
    }
}
