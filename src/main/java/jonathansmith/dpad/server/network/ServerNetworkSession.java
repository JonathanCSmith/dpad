package jonathansmith.dpad.server.network;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.common.database.record.UserRecord;
import jonathansmith.dpad.common.engine.user.UserResponseState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.SessionData;
import jonathansmith.dpad.common.network.packet.play.user.UserAdministrationResponsePacket;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.database.DatabaseConnection;
import jonathansmith.dpad.server.database.DatabaseManager;
import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecord;
import jonathansmith.dpad.server.database.record.user.UserRecordManager;
import jonathansmith.dpad.server.engine.user.UserVerification;
import jonathansmith.dpad.server.network.protocol.ServerHandshakeNetworkProtocol;
import jonathansmith.dpad.server.network.protocol.ServerRuntimeNetworkProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Server side network session
 */
public class ServerNetworkSession extends NetworkSession {

    private final ServerNetworkManager networkManager;

    private DatabaseConnection connection;

    public ServerNetworkSession(ServerEngine engine, ServerNetworkManager manager) {
        super(engine, manager.getSocketAddress(), manager.isLocalConnection(), false);

        this.networkManager = manager;
        this.setNetworkProtocol(new ServerHandshakeNetworkProtocol(this.engine, this, this.isLocalChannel()));
        DatabaseManager.buildServerNetworkSessionDatabaseConnection(this);
    }

    public ServerNetworkManager getNetworkManager() {
        return this.networkManager;
    } // TODO: Should this be accessible

    public void finaliseConnection() {
        this.setNetworkProtocol(new ServerRuntimeNetworkProtocol((ServerEngine) this.engine, this));
    }

    public void assignDatabaseConnection(DatabaseConnection connection) {
        if (this.connection != null) {
            throw new RuntimeException("Cannot re-assign database connections");
        }

        this.connection = connection;
    }

    public void handleUserLogin(boolean newUser, String username, String password) {
        // Handle a new user case
        if (newUser) {
            UserRecord userRecord = UserRecordManager.getInstance().findByUsername(this.connection, username);
            if (userRecord == null) {
                userRecord = new UserRecord();
                userRecord.setUsername(username);
                try {
                    userRecord.setPassword(UserVerification.createSaltedHash(password));
                }

                catch (NoSuchAlgorithmException e) {
                    // Should not happen as its static....
                }

                catch (InvalidKeySpecException e) {
                    // Should not happen as its static....
                }
            }

            else {
                this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.NEW_USER_FAILURE_DUE_TO_NON_UNIQUE_USERNAME), new GenericFutureListener[0]);
                return;
            }

            ServerConfigurationRecord config = ((ServerEngine) this.engine).getServerConfiguration();
            if (config.isUserVerificationRequired()) {
                UserRecordManager.getInstance().saveNew(this.connection, userRecord);
                // TODO: submit message (permissions level admin)
                this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.NEW_USER_PENDING_ADMIN), new GenericFutureListener[0]);
            }

            else {
                this.sessionData.setUserName(username);
                this.sessionData.setUserLoggedIn(true);
                userRecord.setVerified(true);
                UserRecordManager.getInstance().saveNew(this.connection, userRecord);
                this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.LOGIN_SUCCESS), new GenericFutureListener[0]);
            }
        }

        // Handle an existing user case
        else {
            UserRecord user = UserRecordManager.getInstance().findByUsername(this.connection, username);
            if (user == null) {
                this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.EXISTING_USER_FAILURE_UNKNOWN_USERNAME), new GenericFutureListener[0]);
            }

            else {
                try {
                    if (UserVerification.validateUserPassword(password, user.getPassword())) {
                        if (user.isVerified()) {
                            this.sessionData.setUserName(username);
                            this.sessionData.setUserLoggedIn(true);
                            this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.LOGIN_SUCCESS), new GenericFutureListener[0]);
                        }

                        else {
                            this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.EXISTING_USER_STILL_PENDING), new GenericFutureListener[0]);
                        }
                    }

                    else {
                        this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.EXISTING_USER_FAILURE_DUE_TO_INCORRECT_PASSWORD), new GenericFutureListener[0]);
                    }
                }

                catch (InvalidKeySpecException ex) {
                    // Should not happen as its static....
                }

                catch (NoSuchAlgorithmException e) {
                    // Should not happen as its static....
                }
            }
        }
    }

    public void handleUserLogout() {
        // Currently I can't think of a reason why this would fail
        SessionData data = this.getSessionData();
        data.setUserLoggedIn(false);
        data.setUserName("");

        this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.LOGOUT_SUCCESS), new GenericFutureListener[0]);
    }

    public void handleUserChangePassword(String oldPassword, String newPassword) {
        UserRecord userRecord = UserRecordManager.getInstance().findByUsername(this.connection, this.sessionData.getUserName());
        if (userRecord == null) {
            // WTF?!
            this.engine.handleError("Session data is messed up. Current session indicates we are logged in with username: " + this.getSessionData().getUserName() + " but no matching DB record was found!", null);
            return;
        }

        try {
            if (UserVerification.validateUserPassword(oldPassword, userRecord.getPassword())) {
                userRecord.setPassword(UserVerification.createSaltedHash(newPassword));
                this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.CHANGE_PASSWORD_SUCCESS), new GenericFutureListener[0]);
            }

            else {
                this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.CHANGE_PASSWORD_FAILURE), new GenericFutureListener[0]);
            }
        }

        catch (InvalidKeySpecException ex) {
            // Should not happen as its static....
        }

        catch (NoSuchAlgorithmException e) {
            // Should not happen as its static....
        }
    }
}
