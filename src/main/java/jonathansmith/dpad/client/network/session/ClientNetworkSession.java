package jonathansmith.dpad.client.network.session;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.database.UserRecord;
import jonathansmith.dpad.api.events.dataset.ServerExperimentResponseEvent;

import jonathansmith.dpad.common.engine.state.ExperimentAdministrationState;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.handshake.HandshakeStartPacket;
import jonathansmith.dpad.common.network.packet.play.SessionUpdatePacket;
import jonathansmith.dpad.common.network.packet.play.experiment.ExperimentAdministrationPacket;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.network.ClientNetworkManager;
import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Client side network session
 */
public class ClientNetworkSession extends NetworkSession {

    private final ClientNetworkManager networkManager;

    public ClientNetworkSession(ClientEngine engine, ClientNetworkManager manager) {
        super(engine, manager.getSocketAddress(), manager.isLocalConnection(), true, new ClientSessionData());

        this.networkManager = manager;
        this.setNetworkProtocol(new ClientLoginProtocol(engine, this));
        this.scheduleOutboundPacket(new HandshakeStartPacket(engine.getVersion(), this), new GenericFutureListener[0]);
    }

    public void handleExperimentAdministration(ExperimentAdministrationPacket experimentAdministrationPacket) {
        switch (experimentAdministrationPacket.getAdministrationState()) {
            case NOT_LOGGED_IN:
                this.engine.getEventThread().postEvent(new ServerExperimentResponseEvent.StateResponse(ExperimentAdministrationState.NOT_LOGGED_IN));
                break;

            case EXPERIMENT_NAME_NOT_UNIQUE:
                this.engine.getEventThread().postEvent(new ServerExperimentResponseEvent.StateResponse(ExperimentAdministrationState.EXPERIMENT_NAME_NOT_UNIQUE));
                break;

            case EXPERIMENT_CREATION_SUCCESS:
                this.getSessionData().setCurrentExperiment(experimentAdministrationPacket.getExperimentRecord());
                this.engine.getEventThread().postEvent(new ServerExperimentResponseEvent.ExperimentResponse(ExperimentAdministrationState.EXPERIMENT_CREATION_SUCCESS, experimentAdministrationPacket.getExperimentRecord()));
                break;

            case CANNOT_FIND_PROVIDED_EXPERIMENT:
                this.engine.getEventThread().postEvent(new ServerExperimentResponseEvent.StateResponse(ExperimentAdministrationState.CANNOT_FIND_PROVIDED_EXPERIMENT));
                break;

            case EXPERIMENT_SELECTION_SUCCESS:
                this.getSessionData().setCurrentExperiment(experimentAdministrationPacket.getExperimentRecord());
                this.engine.getEventThread().postEvent(new ServerExperimentResponseEvent.ExperimentResponse(ExperimentAdministrationState.EXPERIMENT_SELECTION_SUCCESS, experimentAdministrationPacket.getExperimentRecord()));
                break;

            case SENDING_EXPERIMENTS:
                this.engine.getEventThread().postEvent(new ServerExperimentResponseEvent.ExperimentRecordsResponse(experimentAdministrationPacket.getExperiments()));
                break;

            default:
        }
    }

    public void handleSessionUpdatePacket(SessionUpdatePacket sessionUpdatePacket) {
        switch (sessionUpdatePacket.getUpdateType()) {
            case USER:
                this.sessionData.setCurrentUser((UserRecord) sessionUpdatePacket.getUpdatePayload());
                break;

            case EXPERIMENT:
                this.sessionData.setCurrentExperiment((ExperimentRecord) sessionUpdatePacket.getUpdatePayload());
                break;
        }
    }
}
