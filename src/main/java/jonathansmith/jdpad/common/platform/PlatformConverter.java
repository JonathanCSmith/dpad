package jonathansmith.jdpad.common.platform;

import com.beust.jcommander.IStringConverter;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Converts platform input arguments into runtime parameters
 */
public class PlatformConverter implements IStringConverter<Platform> {

    public Platform convert(String args) {
        return Enum.valueOf(Platform.class, toString().toUpperCase());
    }
}
