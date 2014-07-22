package jonathansmith.dpad.server.gui.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecord;
import jonathansmith.dpad.server.engine.event.ServerDisplayChangeEvent;
import jonathansmith.dpad.server.gui.home.ServerHomeDisplay;

/**
 * Created by Jon on 17/07/2014.
 * <p/>
 * Server configuration editing panel.
 */
public class ServerConfigurationPanel extends DisplayPanel implements ActionListener {

    private final ServerEngine engine;

    private JPanel    contentPane;
    private JCheckBox allowUserAutoVerificationCheckBox;
    private JCheckBox allowGroupAutoCreationCheckBox;
    private JButton   saveButton;

    public ServerConfigurationPanel(ServerEngine engine) {
        this.engine = engine;

        this.saveButton.addActionListener(this);

        ServerConfigurationRecord configuration = this.engine.getServerDatabaseConnection().loadConfiguration();
        this.allowUserAutoVerificationCheckBox.setSelected(configuration.isUserVerificationRequired());
        this.allowGroupAutoCreationCheckBox.setSelected(configuration.isGroupCreationVerificationRequired());
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
        if (ae.getSource() == this.saveButton) {
            ServerConfigurationRecord configurationRecord = this.engine.getServerDatabaseConnection().loadConfiguration();
            configurationRecord.setUserVerificationRequired(this.allowUserAutoVerificationCheckBox.isSelected());
            configurationRecord.setGroupCreationVerificationRequired(this.allowGroupAutoCreationCheckBox.isSelected());
            this.engine.getServerDatabaseConnection().saveConfiguration(configurationRecord);

            this.engine.getEventThread().postEvent(new ServerDisplayChangeEvent(new ServerHomeDisplay(this.engine)));
        }
    }
}
