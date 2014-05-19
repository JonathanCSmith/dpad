package jonathansmith.dpad.common.network.packet.play;

import jonathansmith.dpad.common.network.packet.DisconnectPacket;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Disconnect packet during runtime
 */
public class RuntimeDisconnectPacket extends DisconnectPacket {

    public RuntimeDisconnectPacket() {
        super("");
    }

    public RuntimeDisconnectPacket(String reason) {
        super(reason);
    }
}
