package jonathansmith.dpad.api.client.session;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * All accessible data associated with a session. This should not be modified directly!
 */
public interface ISessionData {

    boolean isUserLoggedIn();

    String getUserName();
}
