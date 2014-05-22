package jonathansmith.dpad.common.network.protocol;

/**
 * Created by Jon on 22/05/2014.
 * <p/>
 * Interface for runtime network protocols
 */
public interface IRuntimeNetworkProtocol extends INetworkProtocol {

    /**
     * Handle a keep alive packet. Common to both client and server
     */
    void handleKeepAlive();
}
