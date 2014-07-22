package jonathansmith.dpad.common.engine.util.configuration;

import java.io.File;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * String property value
 */
public class FileConfigurationValue extends ConfigurationValue<File> {

    private File file;

    public FileConfigurationValue(File file) {
        super();

        this.setPropertyValue(file);
    }

    @Override
    public File getPropertyValue() {
        return this.file;
    }

    @Override
    public void setPropertyValue(File propertyValue) {
        this.file = propertyValue;
    }

    @Override
    public String getPropertyValueForFlatfile() {
        return this.file.getAbsolutePath();
    }

    @Override
    public void setPropertyValueFromFlatfile(String flatfilePropertyValue) {
        this.file = new File(flatfilePropertyValue);
    }
}
