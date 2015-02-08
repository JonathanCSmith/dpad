package jonathansmith.dpad.common.engine.util.configuration;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import jonathansmith.dpad.common.engine.Engine;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Basic configuration for caching engine properties.
 */
public class Configuration {

    private static Configuration instance;
    private static boolean isConfigurationBuilt = false;

    private final File config;
    private final Map<ConfigurationProperty, ConfigurationValue> configurationProperties = new LinkedHashMap<ConfigurationProperty, ConfigurationValue>();

    private boolean changeFlag = true;

    private Configuration(File configFile) {
        this.config = new File(configFile.getAbsolutePath() + "//DPADConfiguration.txt");

        this.configurationProperties.put(ConfigurationProperty.LAST_KNOWN_DATA_LOCATION, new FileConfigurationValue(new File(this.config.getParentFile().getAbsolutePath())));
    }

    public static void build(File executionDomain, Engine engine) {
        if (isConfigurationBuilt) {
            return;
        }

        instance = new Configuration(executionDomain);
        instance.load(engine);
        isConfigurationBuilt = true;
    }

    public static Configuration getInstance() {
        return instance;
    }

    /**
     * This function attempts to load the configuration from its provided location.
     * If it does not exist a default will be created!
     */
    public void load(Engine engine) {
        // Build the file structure if it is not present
        if (this.config.getParentFile() != null) {
            this.config.getParentFile().mkdirs();
        }

        // Read objects
        InputStreamReader input = null;
        BufferedReader buffer = null;

        // Check if the config file exists and build a new one if it doesn't
        try {
            if (!this.config.exists()) {
                if (this.config.createNewFile()) {
                    this.writeData(engine);
                    return;
                }

                else {
                    engine.handleError("Could not create config file!", null);
                    return;
                }
            }
        }

        catch (IOException e) {
            engine.handleError("Could not create config file!", null);
            return;
        }

        try {
            if (this.config.canRead()) {
                input = new InputStreamReader(new FileInputStream(this.config), "UTF-8");
                buffer = new BufferedReader(input);

                String currentLine;

                while (true) {
                    currentLine = buffer.readLine();

                    if (currentLine == null) {
                        break;
                    }

                    String[] parts = currentLine.split("::: ");
                    ConfigurationProperty property = ConfigurationProperty.getPropertyFromTitle(parts[0]);

                    if (property == null) {
                        continue;
                    }

                    ConfigurationValue value;
                    try {
                        value = property.getValueClass().newInstance();
                        value.setPropertyValueFromFlatfile(parts[1]);
                    }

                    catch (InstantiationException ex) {
                        engine.warn("Configuration value read error", ex);
                        continue;
                    }

                    catch (IllegalAccessException ex) {
                        engine.warn("Configuration value read error", ex);
                        continue;
                    }

                    this.configurationProperties.put(property, value);
                }
            }
        }

        catch (IOException ex) {
            engine.handleError("Could not load config file!", ex);
        }

        finally {
            if (buffer != null) {
                try {
                    buffer.close();
                }

                catch (IOException ex) {

                }
            }

            if (input != null) {
                try {
                    input.close();
                }

                catch (IOException ex) {

                }
            }
        }

        this.resetChangeFlag();
    }

    public void save(Engine engine) {
        if (!this.changeFlag) {
            return;
        }

        if (this.config.getParentFile() != null) {
            this.config.getParentFile().mkdirs();
        }

        try {
            if (!this.config.exists() && !this.config.createNewFile()) {
                engine.handleError("Could not create config file!", null);
                return;
            }
        }

        catch (IOException ex) {
            engine.handleError("Could not create config file", null);
        }

        this.writeData(engine);
    }

    private void writeData(Engine engine) {
        try {
            if (this.config.canWrite()) {
                FileOutputStream fos = new FileOutputStream(this.config);
                BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

                for (Map.Entry<ConfigurationProperty, ConfigurationValue> entry : this.configurationProperties.entrySet()) {
                    buffer.write(entry.getKey().getCategoryTitle() + "::: " + entry.getValue().getPropertyValueForFlatfile());
                    buffer.newLine();
                }

                buffer.close();
                fos.close();
            }
        }

        catch (IOException ex) {
            engine.handleError("Could not save configuration!", ex);
        }
    }

    public ConfigurationValue getConfigValue(ConfigurationProperty property) {
        return this.configurationProperties.get(property);
    }

    public void setConfigurationValue(ConfigurationProperty property, ConfigurationValue value) {
        if (value.getClass().equals(property.getValueClass())) {
            this.configurationProperties.put(property, value);
            this.setChanged();
        }
    }

    private void setChanged() {
        this.changeFlag = true;
    }

    private void resetChangeFlag() {
        this.changeFlag = false;
    }
}
