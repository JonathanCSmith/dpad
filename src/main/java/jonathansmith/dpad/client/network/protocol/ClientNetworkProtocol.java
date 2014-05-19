package jonathansmith.dpad.client.network.protocol;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.api.common.engine.event.IEventThread;

import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

import jonathansmith.dpad.client.engine.event.ClientDisplayChangeEvent;
import jonathansmith.dpad.client.gui.ClientDisconnectDisplay;

/**
 * Created by Jon on 19/05/2014.
 * <p/>
 * Client side specific protocols
 */
public abstract class ClientNetworkProtocol extends NetworkProtocol {

    private final String protocolName;

    public ClientNetworkProtocol(IEngine engine, NetworkSession session, String protocolName) {
        super(engine, session);

        this.protocolName = protocolName;
    }

    public void handleDisconnect(String reason) {
        this.networkSession.closeChannel(reason);
    }

    @Override
    public String getProtocolName() {
        return this.protocolName;
    }

    @Override
    public void pulseScheduledProtocolTasks() {
    }

    @Override
    public void onDisconnect(String exitMessage) {
        IEventThread eventThread = this.engine.getEventThread();
        eventThread.postEvent(new ClientDisplayChangeEvent(new ClientDisconnectDisplay()));
        this.engine.handleShutdown(exitMessage);
    }
}
