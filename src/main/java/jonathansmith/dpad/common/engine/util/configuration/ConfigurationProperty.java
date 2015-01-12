package jonathansmith.dpad.common.engine.util.configuration;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Configuration Properties available
 */
public enum ConfigurationProperty {

    LAST_KNOWN_DATA_LOCATION("Last known data location", FileConfigurationValue.class);
    private final String                              categoryTitle;
    private final Class<? extends ConfigurationValue> valueClass;

    private ConfigurationProperty(String categoryTitle, Class<? extends ConfigurationValue> clazz) {
        this.categoryTitle = categoryTitle;
        this.valueClass = clazz;
    }

    public static ConfigurationProperty getPropertyFromTitle(String title) {
        for (ConfigurationProperty property : ConfigurationProperty.values()) {
            if (property.getCategoryTitle().contentEquals(title)) {
                return property;
            }
        }

        return null;
    }

    public String getCategoryTitle() {
        return this.categoryTitle;
    }

    public Class<? extends ConfigurationValue> getValueClass() {
        return this.valueClass;
    }
}
