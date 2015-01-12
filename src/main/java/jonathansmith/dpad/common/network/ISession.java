package jonathansmith.dpad.common.network;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.common.network.packet.Packet;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Small utility interface for dealing with sessions. Prevents some access security isssues.
 */
public interface ISession {

    void scheduleOutboundPacket(Packet packet, GenericFutureListener[] listeners);
}
