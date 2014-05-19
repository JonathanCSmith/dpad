package jonathansmith.dpad.common.network.packet.login;

import jonathansmith.dpad.common.network.packet.DisconnectPacket;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Disconnect packet for login
 */
public class LoginDisconnectPacket extends DisconnectPacket {

    public LoginDisconnectPacket() {
        super("");
    }

    public LoginDisconnectPacket(String reason) {
        super(reason);
    }
}
