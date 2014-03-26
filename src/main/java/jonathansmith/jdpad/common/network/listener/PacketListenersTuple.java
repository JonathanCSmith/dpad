package jonathansmith.jdpad.common.network.listener;

import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.jdpad.common.network.packet.Packet;

/**
 * Created by Jon on 26/03/14.
 *
 * Generic listener for unopened channels
 */
public class PacketListenersTuple {

    private final Packet packet;
    private final GenericFutureListener[] listeners;

    public PacketListenersTuple(Packet packet, GenericFutureListener[] listeners) {
        this.packet = packet;
        this.listeners = listeners;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public GenericFutureListener[] getListeners() {
        return this.listeners;
    }
}
