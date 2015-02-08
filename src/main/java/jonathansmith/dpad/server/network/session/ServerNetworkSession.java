package jonathansmith.dpad.server.network.session;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.database.*;
import jonathansmith.dpad.api.plugins.data.Dataset;

import jonathansmith.dpad.common.engine.state.DatasetAdministationState;
import jonathansmith.dpad.common.engine.state.ExperimentAdministrationState;
import jonathansmith.dpad.common.engine.state.UserResponseState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.SessionData;
import jonathansmith.dpad.common.network.packet.play.dataset.DatasetAdministrationPacket;
import jonathansmith.dpad.common.network.packet.play.experiment.ExperimentAdministrationPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserAdministrationResponsePacket;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.database.DatabaseConnection;
import jonathansmith.dpad.server.database.DatabaseManager;
import jonathansmith.dpad.server.database.record.dataset.DatasetRecordManager;
import jonathansmith.dpad.server.database.record.experiment.ExperimentRecordManager;
import jonathansmith.dpad.server.database.record.loadingplugin.LoadingPluginRecordManager;
import jonathansmith.dpad.server.database.record.measurement.MeasurementRecordManager;
import jonathansmith.dpad.server.database.record.sample.SampleRecordManager;
import jonathansmith.dpad.server.database.record.serverconfiguration.ServerConfigurationRecord;
import jonathansmith.dpad.server.database.record.user.UserRecordManager;
import jonathansmith.dpad.server.engine.user.UserVerification;
import jonathansmith.dpad.server.network.ServerNetworkManager;
import jonathansmith.dpad.server.network.protocol.ServerHandshakeNetworkProtocol;
import jonathansmith.dpad.server.network.protocol.ServerNetworkProtocol;
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
        super(engine, manager.getSocketAddress(), manager.isLocalConnection(), false, new ServerSessionData());

        ((ServerSessionData) this.sessionData).setSession(this);

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
                userRecord.setVerified(true);
                UserRecordManager.getInstance().saveNew(this.connection, userRecord);
                this.sessionData.setCurrentUser(userRecord);
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
                            this.sessionData.setCurrentUser(user);
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
        this.sessionData.setCurrentUser(null);
        this.scheduleOutboundPacket(new UserAdministrationResponsePacket(UserResponseState.LOGOUT_SUCCESS), new GenericFutureListener[0]);
    }

    public void handleUserChangePassword(String oldPassword, String newPassword) {
        UserRecord userRecord = UserRecordManager.getInstance().findByUsername(this.connection, this.sessionData.getCurrentUserName());
        if (userRecord == null) {
            // WTF?!
            this.engine.handleError("Session data is messed up. Current session indicates we are logged in with username: " + this.getSessionData().getCurrentUserName() + " but no matching DB record was found!", null);
            return;
        }

        try {
            if (UserVerification.validateUserPassword(oldPassword, userRecord.getPassword())) {
                userRecord.setPassword(UserVerification.createSaltedHash(newPassword));
                UserRecordManager.getInstance().save(this.connection, userRecord);
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

    public void handleNewExperiment(ExperimentRecord experimentRecord) {
        if (!this.getSessionData().isUserLoggedIn()) {
            this.engine.error("Somehow your session state is messed up! You are not logged in!", null);
            this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.NOT_LOGGED_IN), new GenericFutureListener[0]);
            return;
        }

        UserRecord user = UserRecordManager.getInstance().findByUsername(this.connection, this.getSessionData().getCurrentUserName());
        Set<ExperimentRecord> currentExperiments = UserRecordManager.getInstance().fetchExperiments(this.connection, user).getExperiments();
        boolean hasName = false;
        for (ExperimentRecord record : currentExperiments) {
            if (record.getExperimentName().contentEquals(experimentRecord.getExperimentName())) {
                hasName = true;
                break;
            }
        }

        if (hasName) {
            this.engine.error("Error creating new experiment, the name is not unique!", null);
            this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.EXPERIMENT_NAME_NOT_UNIQUE), new GenericFutureListener[0]);
        }

        else {
            ExperimentRecordManager.getInstance().saveNew(this.connection, experimentRecord);
            user.addExperiment(experimentRecord);
            UserRecordManager.getInstance().save(this.connection, user);
            SessionData data = this.getSessionData();
            data.setCurrentExperiment(experimentRecord);
            this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.EXPERIMENT_CREATION_SUCCESS, experimentRecord), new GenericFutureListener[0]);
        }
    }

    public void handleCurrentExperimentSelection(ExperimentRecord experimentRecord) {
        if (!this.getSessionData().isUserLoggedIn()) {
            this.engine.error("Somehow your session state is messed up! You are not logged in!", null);
            this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.NOT_LOGGED_IN), new GenericFutureListener[0]);
            return;
        }

        UserRecord user = UserRecordManager.getInstance().findByUsername(this.connection, this.getSessionData().getCurrentUserName());
        Set<ExperimentRecord> currentExperiments = UserRecordManager.getInstance().fetchExperiments(this.connection, user).getExperiments();
        boolean hasName = false;
        for (ExperimentRecord record : currentExperiments) {
            if (record.getExperimentName().contentEquals(experimentRecord.getExperimentName())) {
                hasName = true;
                break;
            }
        }

        if (hasName) {
            SessionData data = this.getSessionData();
            data.setCurrentExperiment(experimentRecord);
            this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.EXPERIMENT_SELECTION_SUCCESS, experimentRecord), new GenericFutureListener[0]);
        }

        else {
            this.engine.error("Attempted to load an experiment that did not exist!", null);
            this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.CANNOT_FIND_PROVIDED_EXPERIMENT, experimentRecord), new GenericFutureListener[0]);
        }
    }

    public void handleRequestForExperiments() {
        if (!this.getSessionData().isUserLoggedIn()) {
            this.engine.error("Somehow your session state is messed up! You are not logged in!", null);
            this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.NOT_LOGGED_IN), new GenericFutureListener[0]);
            return;
        }

        UserRecord user = UserRecordManager.getInstance().findByUsername(this.connection, this.getSessionData().getCurrentUserName());
        HashSet<ExperimentRecord> currentExperiments = new HashSet<ExperimentRecord>(UserRecordManager.getInstance().fetchExperiments(this.connection, user).getExperiments());
        this.scheduleOutboundPacket(new ExperimentAdministrationPacket(ExperimentAdministrationState.SENDING_EXPERIMENTS, currentExperiments), new GenericFutureListener[0]);
    }

    public void handleNewDataset(Dataset dataset) {
        if (dataset == null) {
            return;
        }

        DatasetRecord datasetRecord = new DatasetRecord();
        SampleRecord sampleRecord = new SampleRecord();
        sampleRecord.setSampleName(dataset.getSampleName());
        SampleRecordManager.getInstance().saveNew(this.connection, sampleRecord);
        datasetRecord.setSampleRecord(sampleRecord);

        MeasurementConditionRecord measurementRecord = new MeasurementConditionRecord();
        measurementRecord.setMeasurementType(dataset.getSampleMeasurementCondition());
        MeasurementRecordManager.getInstance().saveNew(this.connection, measurementRecord);
        datasetRecord.setMeasurementType(measurementRecord);

        LoadingPluginRecord pluginRecord = new LoadingPluginRecord();
        pluginRecord.setPluginName(dataset.getPluginSource().getPluginName());
        pluginRecord.setPluginDescription(dataset.getPluginSource().getPluginDescription());
        pluginRecord.setPluginAuthor(dataset.getPluginSource().getPluginAuthor());
        pluginRecord.setPluginOrganisation(dataset.getPluginSource().getPluginOrganisation());
        datasetRecord.setPluginSource(pluginRecord);
        LoadingPluginRecordManager.getInstance().saveNew(this.connection, pluginRecord);

        TreeMap<Integer, Integer> data = dataset.getMeasurements();
        datasetRecord.setDataSize(data.size());
        int index = 0;
        for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
            datasetRecord.addMeasurement(index, new int[]{entry.getKey(), entry.getValue()});
            index++;
        }

        DatasetRecordManager.getInstance().saveNew(this.connection, datasetRecord);

        this.sessionData.getCurrentExperiment().addDataset(datasetRecord);
        ExperimentRecordManager.getInstance().save(this.connection, this.sessionData.getCurrentExperiment());
    }

    public void retrieveDatasetsFromExperiments(HashSet<ExperimentRecord> interestedRecords) {
        if (interestedRecords.isEmpty()) {
            return;
        }

        HashSet<DatasetRecord> datasets = new HashSet<DatasetRecord>();
        for (ExperimentRecord record : interestedRecords) {
            Set<DatasetRecord> nonHash = ExperimentRecordManager.getInstance().fetchDatasets(this.connection, record).getDatasets();
            datasets.addAll(nonHash);
        }
        this.scheduleOutboundPacket(new DatasetAdministrationPacket(DatasetAdministationState.RETURNING_DATASETS, datasets), new GenericFutureListener[0]);
    }

    public void retrieveFullDatasetInformation(HashSet<DatasetRecord> interestedRecords) {
        if (interestedRecords.isEmpty()) {
            return;
        }

        HashSet<DatasetRecord> fullDatasets = new HashSet<DatasetRecord>();
        for (DatasetRecord record : interestedRecords) {
            fullDatasets.add(DatasetRecordManager.getInstance().fetchFull(this.connection, record));
        }

        this.scheduleOutboundPacket(new DatasetAdministrationPacket(DatasetAdministationState.FULL_DATA, fullDatasets), new GenericFutureListener[0]);
    }

    public void sendDisconnectPacket(String reason, GenericFutureListener[] listeners) {
        ((ServerNetworkProtocol) this.getNetworkProtocol()).sendDisconnectPacket(reason, listeners);
    }
}
