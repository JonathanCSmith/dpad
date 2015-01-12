package jonathansmith.dpad.common.network;

import jonathansmith.dpad.api.common.network.session.ISessionData;
import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.database.UserRecord;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Common implementation of session data. Should not be available to API users!
 * <p/>
 * TODO: Move to a data setting based flag set. Move to providing direct record access IF this is not available to the API
 */
public abstract class SessionData implements ISessionData {

    protected boolean isLoggedIn = false;
    protected UserRecord userRecord;

    protected boolean isRunningExperiment = false;
    protected ExperimentRecord experimentRecord;

    @Override
    public boolean isUserLoggedIn() {
        return this.isLoggedIn;
    }

    public UserRecord getCurrentUser() {
        return this.userRecord;
    }

    public abstract void setCurrentUser(UserRecord record);

    @Override
    public String getCurrentUserName() {
        if (this.userRecord == null) {
            return "";
        }

        return this.userRecord.getUsername();
    }

    @Override
    public boolean isRunningExperiment() {
        return this.isRunningExperiment;
    }

    public ExperimentRecord getCurrentExperiment() {
        return this.experimentRecord;
    }

    public abstract void setCurrentExperiment(ExperimentRecord record);

    @Override
    public String getCurrentExperimentName() {
        if (this.experimentRecord == null) {
            return "";
        }

        return this.experimentRecord.getExperimentName();
    }

    public enum SessionDataType {
        EXPERIMENT, USER
    }
}
