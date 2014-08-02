package jonathansmith.dpad.common.engine.user;

/**
 * Created by Jon on 24/07/2014.
 * <p/>
 * User response states for communication between client and server
 */
public enum UserResponseState {
    NEW_USER_FAILURE_DUE_TO_NON_UNIQUE_USERNAME,
    NEW_USER_PENDING_ADMIN,
    EXISTING_USER_STILL_PENDING,
    EXISTING_USER_FAILURE_DUE_TO_INCORRECT_PASSWORD,
    EXISTING_USER_FAILURE_UNKNOWN_USERNAME,
    LOGIN_SUCCESS,
    LOGOUT_SUCCESS, CHANGE_PASSWORD_FAILURE, CHANGE_PASSWORD_SUCCESS,
}
