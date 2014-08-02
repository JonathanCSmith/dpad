package jonathansmith.dpad.client.engine.executor.user;

import java.util.ArrayList;
import java.util.List;

import jonathansmith.dpad.api.common.engine.event.IEventListener;

import jonathansmith.dpad.common.engine.event.Event;
import jonathansmith.dpad.common.engine.event.gui.ProgressBarUpdateEvent;
import jonathansmith.dpad.common.engine.executor.Task;
import jonathansmith.dpad.common.engine.user.UserResponseState;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.ServerUserResponseEvent;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Wait task for server response to user request
 */
public class UserResponseWaitTask extends Task implements IEventListener {

    private static final String                       TASK_NAME = "User responce wait";
    private static final List<Class<? extends Event>> EVENTS    = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(ServerUserResponseEvent.class);
    }

    private boolean isWaiting = true;
    private boolean isKilled  = false;

    public UserResponseWaitTask(ClientEngine engine) {
        super(TASK_NAME, engine);
    }

    @Override
    protected void runTask() {
        this.loggingEngine.getEventThread().addEventListener(this);
        while (this.isWaiting && !this.isKilled) {
            try {
                Thread.sleep(100);
            }

            catch (InterruptedException ex) {
                // No matter - we dont need to pass this up
            }
        }

        this.loggingEngine.getEventThread().removeListener(this);
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent("Rebuilding Client Home", 0, 3, 3));
    }

    @Override
    protected void killTask() {
        this.isKilled = true;
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        ServerUserResponseEvent evt = (ServerUserResponseEvent) event;
        UserResponseState state = evt.getState();

        String statement = "";
        switch (state) {
            case NEW_USER_FAILURE_DUE_TO_NON_UNIQUE_USERNAME:
                statement = "Failure to create user as the username is not unique";
                break;

            case NEW_USER_PENDING_ADMIN:
                statement = "Your request is undergoing admin validation. This may take some time.";
                break;

            case EXISTING_USER_STILL_PENDING:
                statement = "Your request is still undergoing admin validation. Try again later!";
                break;

            case EXISTING_USER_FAILURE_UNKNOWN_USERNAME:
                statement = "Your username was not recognised. Contact your system administrator.";
                break;

            case EXISTING_USER_FAILURE_DUE_TO_INCORRECT_PASSWORD:
                statement = "Your password was incorrect. Login failure";
                break;

            case LOGIN_SUCCESS:
                statement = "Login was successful";
                break;

            case LOGOUT_SUCCESS:
                statement = "Logout was successful";
                break;

            case CHANGE_PASSWORD_FAILURE:
                statement = "Password change failed due to your old password being incorrect.";
                break;

            case CHANGE_PASSWORD_SUCCESS:
                statement = "Your password has been successfully changed.";
                break;
        }

        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(statement, 0, 3, 2));
        this.isWaiting = false;
    }
}
