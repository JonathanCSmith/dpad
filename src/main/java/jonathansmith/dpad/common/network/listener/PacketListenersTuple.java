package jonathansmith.dpad.common.network.listener;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.common.network.packet.Packet;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Generic listener for unopened channels
 */
public class PacketListenersTuple {

    private final Packet                                packet;
    private final GenericFutureListener<Future<Void>>[] listeners;

    public PacketListenersTuple(Packet packet, GenericFutureListener[] listeners) {
        this.packet = packet;
        this.listeners = listeners;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public GenericFutureListener<Future<Void>>[] getListeners() {
        return this.listeners;
    }
}
