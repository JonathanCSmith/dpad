package jonathansmith.dpad.server.network.protocol;

import java.util.HashSet;

import io.netty.util.concurrent.GenericFutureListener;

import org.apache.commons.lang3.Validate;

import jonathansmith.dpad.api.database.DatasetRecord;
import jonathansmith.dpad.api.database.ExperimentRecord;

import jonathansmith.dpad.common.network.ConnectionState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.DisconnectPacket;
import jonathansmith.dpad.common.network.packet.play.KeepAlivePacket;
import jonathansmith.dpad.common.network.packet.play.dataset.DatasetAdministrationPacket;
import jonathansmith.dpad.common.network.packet.play.experiment.ExperimentAdministrationPacket;
import jonathansmith.dpad.common.network.packet.play.plugin.DatasetPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserChangePasswordPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserLoginPacket;
import jonathansmith.dpad.common.network.protocol.IRuntimeNetworkProtocol;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.network.session.ServerNetworkSession;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * General runtime network protocol for the associated client
 */
public class ServerRuntimeNetworkProtocol extends ServerNetworkProtocol implements IRuntimeNetworkProtocol {

    private static final String PROTOCOL_NAME    = "Server Runtime Protocol";
    private static final long   KEEP_ALIVE_DELAY = 300L;

    private final ServerEngine   engine;
    private final NetworkSession network_session;

    private boolean sendKeepAlives    = false;
    private long    lastKeepAliveTime = 0L;

    public ServerRuntimeNetworkProtocol(ServerEngine engine, NetworkSession session) {
        this.engine = engine;
        this.network_session = session;
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public void onConnectionStateTransition(ConnectionState connectionState, ConnectionState connectionState1) {
        try {
            Validate.validState(connectionState1 == ConnectionState.RUNTIME, "Unexpected protocol change. Attempted to change to %s from %s", connectionState1.toString(), connectionState.toString());
        }

        catch (IllegalStateException ex) {
            this.engine.error("Invalid connection state transition", ex);
        }
    }

    @Override
    public void pulseScheduledProtocolTasks() {
        if (!this.sendKeepAlives) {
            return;
        }

        if (this.lastKeepAliveTime == 0L) {
            this.lastKeepAliveTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - this.lastKeepAliveTime > KEEP_ALIVE_DELAY) {
            this.lastKeepAliveTime = System.currentTimeMillis();
            this.network_session.scheduleOutboundPacket(new KeepAlivePacket(), new GenericFutureListener[0]);
        }
    }

    @Override
    public void onDisconnect(String exitMessage) {
        this.engine.info(this.network_session.buildSessionInformation() + " lost connection: " + exitMessage, null);
    }

    @Override
    public void sendDisconnectPacket(String reason, GenericFutureListener[] listeners) {
        this.network_session.scheduleOutboundPacket(new DisconnectPacket(reason), listeners);
    }

    @Override
    public void handleKeepAlive() {
        // Do nothing
    }

    @Override
    public void handleExperimentAdministration(ExperimentAdministrationPacket experimentAdministrationPacket) {
        switch (experimentAdministrationPacket.getAdministrationState()) {
            case NEW_EXPERIMENT:
                ((ServerNetworkSession) this.network_session).handleNewExperiment(experimentAdministrationPacket.getExperimentRecord());
                break;

            case SETTING_CURRENT_EXPERIMENT:
                ((ServerNetworkSession) this.network_session).handleCurrentExperimentSelection(experimentAdministrationPacket.getExperimentRecord());
                break;

            case REQUESTING_EXPERIMENTS:
                ((ServerNetworkSession) this.network_session).handleRequestForExperiments();
                break;

            default:
        }
    }

    @Override
    public void handleDatasetAdministrationPacket(DatasetAdministrationPacket datasetAdministrationPacket) {
        switch (datasetAdministrationPacket.getPacketState()) {
            case REQUESTING_EXPERIMENT_DATASETS:
                ((ServerNetworkSession) this.network_session).retrieveDatasetsFromExperiments((HashSet<ExperimentRecord>) datasetAdministrationPacket.getInterestedRecords());
                break;

            case GET_FULL_DATA:
                ((ServerNetworkSession) this.network_session).retrieveFullDatasetInformation((HashSet<DatasetRecord>) datasetAdministrationPacket.getInterestedRecords());
        }
    }

    public void handleLoginConfirmation() {
        this.sendKeepAlives = true;
    }

    public void handleUserLogin(UserLoginPacket userLoginPacket) {
        if (this.network_session.getSessionData().isUserLoggedIn()) {
            // How the fuck?!
            // MAYBE: RE-SYNC as opposed to failure?
            this.engine.handleError("Something is really wrong with the session data. Currently the user: " + this.network_session.getSessionData().getCurrentUserName() + " is attempting to login as: " + userLoginPacket.getUsername(), new RuntimeException());
            return;
        }

        ((ServerNetworkSession) this.network_session).handleUserLogin(userLoginPacket.isNewUser(), userLoginPacket.getUsername(), userLoginPacket.getPassword());
    }

    public void handleUserLogout() {
        if (!this.network_session.getSessionData().isUserLoggedIn()) {
            // How the fuck?!
            // MAYBE: RE-SYNC as opposed to failure?
            this.engine.handleError("Something is really wrong with the session data. The session: " + this.network_session.getEngineAssignedUUID() + " is attempting to logout without being logged in!", new RuntimeException());
            return;
        }

        ((ServerNetworkSession) this.network_session).handleUserLogout();
    }

    public void handleUserChangePassword(UserChangePasswordPacket userChangePasswordPacket) {
        if (!this.network_session.getSessionData().isUserLoggedIn()) {
            // How the fuck?!
            // MAYBE: RE-SYNC as opposed to failure?
            this.engine.handleError("Something is really wrong with the session data. The session: " + this.network_session.getEngineAssignedUUID() + " is attempting to change it's password without being logged in!", new RuntimeException());
            return;
        }

        ((ServerNetworkSession) this.network_session).handleUserChangePassword(userChangePasswordPacket.getOldPassword(), userChangePasswordPacket.getNewPassword());
    }

    public void handleNewDataset(DatasetPacket datasetPacket) {
        if (!this.network_session.getSessionData().isRunningExperiment()) {
            // HOW THE FUCK?
            this.engine.handleError("Something is really wrong with the session data. The session: " + this.network_session.getEngineAssignedUUID() + " is attempting to add a dataset without having an experiment selected!", new RuntimeException());
            return;
        }

        ((ServerNetworkSession) this.network_session).handleNewDataset(datasetPacket.getDataset());
    }
}
