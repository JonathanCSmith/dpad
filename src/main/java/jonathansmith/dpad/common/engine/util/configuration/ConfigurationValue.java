package jonathansmith.dpad.common.engine.util.configuration;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Common parent for all configuration properties.
 */
public abstract class ConfigurationValue<T> {

    public ConfigurationValue() {
    }

    public abstract T getPropertyValue();

    public abstract void setPropertyValue(T propertyValue);

    public abstract String getPropertyValueForFlatfile();

    public abstract void setPropertyValueFromFlatfile(String flatfilePropertyValue);
}
