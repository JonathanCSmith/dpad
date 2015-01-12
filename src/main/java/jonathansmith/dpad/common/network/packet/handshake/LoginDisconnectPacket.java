package jonathansmith.dpad.common.network.packet.handshake;

import jonathansmith.dpad.common.network.packet.DisconnectPacket;

/**
 * Created by Jon on 29/09/2014.
 * <p/>
 * Login disconnect packet handles disconnect during the login stages
 */
public class LoginDisconnectPacket extends DisconnectPacket {
    public LoginDisconnectPacket(String reason) {
        super(reason);
    }
}
