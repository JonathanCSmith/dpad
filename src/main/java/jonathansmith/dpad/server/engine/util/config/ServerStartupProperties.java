package jonathansmith.dpad.server.engine.util.config;

/**
 * Created by Jon on 13/07/2014.
 * <p/>
 * Server configuration. Transient object for correct server setup.
 */
public class ServerStartupProperties {

    private final boolean isNewServer;
    private final String  superUsername;
    private final String  superUserPassword;
    private final boolean isAutoVerificationEnabled;

    public ServerStartupProperties(boolean isNew, String superUser, String password, boolean isAutoVerificationEnabled) {
        this.isNewServer = isNew;
        this.superUsername = superUser;
        this.superUserPassword = password;
        this.isAutoVerificationEnabled = isAutoVerificationEnabled;
    }

    public boolean isNewServer() {
        return this.isNewServer;
    }

    public String getSuperUsername() {
        return this.superUsername;
    }

    public String getSuperUserPassword() {
        return this.superUserPassword;
    }

    public boolean isAutoVerificationEnabled() {
        return this.isAutoVerificationEnabled;
    }
}
