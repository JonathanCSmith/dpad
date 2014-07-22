package jonathansmith.dpad.client.gui.startup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import jonathansmith.dpad.common.engine.util.configuration.Configuration;
import jonathansmith.dpad.common.engine.util.configuration.ConfigurationProperty;
import jonathansmith.dpad.common.engine.util.configuration.FileConfigurationValue;
import jonathansmith.dpad.common.gui.display.DisplayPanel;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ClientStartupPropertiesFinishEvent;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Client configuration panel
 */
public class ClientStartupPropertiesPanel extends DisplayPanel implements ActionListener {

    private final ClientEngine  engine;
    private final Configuration configuration;

    private File dataFile = null;

    private JButton    setDPADPathButton;
    private JTextField dataPath;
    private JPanel     contentPane;

    public ClientStartupPropertiesPanel(ClientEngine engine) {
        this.engine = engine;
        this.configuration = Configuration.getInstance();

        this.setDPADPathButton.addActionListener(this);

        this.dataFile = ((FileConfigurationValue) this.configuration.getConfigValue(ConfigurationProperty.LAST_KNOWN_DATA_LOCATION)).getPropertyValue();
        this.dataPath.setText(this.dataFile.getAbsolutePath());
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = chooser.showOpenDialog(this.contentPane);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (this.dataFile != chooser.getSelectedFile()) {
                this.configuration.setConfigurationValue(ConfigurationProperty.LAST_KNOWN_DATA_LOCATION, new FileConfigurationValue(chooser.getSelectedFile()));
                this.configuration.save(this.engine);
            }

            this.engine.getEventThread().postEvent(new ClientStartupPropertiesFinishEvent());
        }
    }
}
