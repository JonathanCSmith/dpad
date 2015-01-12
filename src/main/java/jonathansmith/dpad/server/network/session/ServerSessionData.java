package jonathansmith.dpad.server.network.session;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.database.ExperimentRecord;
import jonathansmith.dpad.api.database.UserRecord;

import jonathansmith.dpad.common.network.SessionData;
import jonathansmith.dpad.common.network.packet.play.SessionUpdatePacket;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Server side session data
 */
public class ServerSessionData extends SessionData {

    private ServerNetworkSession session;

    @Override
    public void setCurrentUser(UserRecord record) {
        this.userRecord = record;
        this.session.scheduleOutboundPacket(new SessionUpdatePacket(SessionDataType.USER, this.getCurrentUser()), new GenericFutureListener[0]);
        this.isLoggedIn = record != null;
    }

    @Override
    public void setCurrentExperiment(ExperimentRecord record) {
        this.experimentRecord = record;
        this.session.scheduleOutboundPacket(new SessionUpdatePacket(SessionDataType.EXPERIMENT, this.getCurrentExperiment()), new GenericFutureListener[0]);
        this.isRunningExperiment = record != null;
    }

    public void setSession(ServerNetworkSession session) {
        this.session = session;
    }
}
