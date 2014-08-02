package jonathansmith.dpad.common.network;

import jonathansmith.dpad.api.common.network.session.ISessionData;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Common implementation of session data. Should not be available to API users!
 */
public class SessionData implements ISessionData {

    private boolean isUserLoggedIn = false;
    private String  userName       = "";

    @Override
    public boolean isUserLoggedIn() {
        return this.isUserLoggedIn;
    }

    public void setUserLoggedIn(boolean flag) {
        this.isUserLoggedIn = flag;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }
}
