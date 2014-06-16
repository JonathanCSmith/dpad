package jonathansmith.dpad.client.network.session;

import jonathansmith.dpad.api.client.session.ISessionData;

/**
 * Created by Jon on 16/06/2014.
 * <p/>
 * Client side session data.
 */
public class ClientSessionData implements ISessionData {

    // User data
    private boolean isUserLoggedIn = false;
    private String  username       = "";


    @Override
    public boolean isUserLoggedIn() {
        return this.isUserLoggedIn;
    }

    @Override
    public String getUserName() {
        return this.username;
    }
}
