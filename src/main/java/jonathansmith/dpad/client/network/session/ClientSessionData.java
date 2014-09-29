package jonathansmith.dpad.client.network.session;

import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.database.UserRecord;

import jonathansmith.dpad.common.network.SessionData;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Client side session data
 */
public class ClientSessionData extends SessionData {

    @Override
    public void setCurrentUser(UserRecord record) {
        this.userRecord = record;
        this.isLoggedIn = record != null;
    }

    @Override
    public void setCurrentExperiment(ExperimentRecord record) {
        this.experimentRecord = record;
        this.isRunningExperiment = record != null;
    }
}
