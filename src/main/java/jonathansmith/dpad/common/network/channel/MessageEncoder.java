package jonathansmith.dpad.common.network.channel;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Encodes packets into their packet buffer form
 */
public class MessageEncoder extends MessageToByteEncoder {

    private final IEngine engine;

    public MessageEncoder(IEngine engine) {
        this.engine = engine;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        Packet packet = (Packet) o;
        Integer packetId = channelHandlerContext.channel().attr(NetworkSession.WHITELISTED_SENDABLE_PACKETS_ATTRIBUTE_KEY).get().inverse().get(packet.getClass());
        this.engine.debug("Encoding: " + packet.getClass() + " on connection state: " + channelHandlerContext.channel().attr(NetworkSession.CONNECTION_STATE_ATTRIBUTE_KEY).get() + " with a payload of: " + packet.payloadToString(), null);

        if (packetId == null) {
            IOException ex = new IOException("Unregistered packet detected");
            this.engine.error("Error in message encoder", ex);
            throw ex;
        }

        else {
            PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
            packetBuffer.writeVarIntToBuffer(packetId.intValue());
            packet.writePacketData(packetBuffer);
        }
    }
}
