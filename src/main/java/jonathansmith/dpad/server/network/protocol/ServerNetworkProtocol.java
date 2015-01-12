package jonathansmith.dpad.server.network.protocol;

import io.netty.util.concurrent.GenericFutureListener;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Common methods for server side network protocols
 */
public abstract class ServerNetworkProtocol {

    /**
     * Used to send a runtime aware packet for disconnection
     *
     * @param listeners
     */
    public abstract void sendDisconnectPacket(String reason, GenericFutureListener[] listeners);
}
