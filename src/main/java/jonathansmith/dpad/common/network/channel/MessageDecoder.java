package jonathansmith.dpad.common.network.channel;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import jonathansmith.dpad.api.common.engine.IEngine;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Decodes packet buffer messages into their packet form.
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private final IEngine engine;

    public MessageDecoder(IEngine engine) {
        this.engine = engine;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
        if (byteBuf.readableBytes() != 0) {
            PacketBuffer packetBuffer = new PacketBuffer(byteBuf);

            int packetId = packetBuffer.readVarIntFromBuffer();
            Packet packet;
            try {
                packet = Packet.getEmptyPacket(channelHandlerContext.channel().attr(NetworkSession.WHITELISTED_RECEIVABLE_PACKETS_ATTRIBUTE_KEY).get(), packetId);
            }

            catch (Exception ex) {
                this.engine.error("Could not create the indicated packet...", ex);
                throw ex;
            }

            if (packet == null) {
                IOException ex = new IOException("Bad packet id...");
                this.engine.error("Exception in message decoder", ex);
                throw ex;
            }

            else {
                packet.readPacketData(packetBuffer);

                if (packetBuffer.readableBytes() > 0) {
                    IOException ex = new IOException("Packet was larger than expected...");
                    this.engine.error("Exception in message decoder", ex);
                    throw ex;
                }

                else {
                    objects.add(packet);

                    this.engine.debug(packet.getClass() + " was received and successfully decoded on connection state: " + channelHandlerContext.channel().attr(NetworkSession.CONNECTION_STATE_ATTRIBUTE_KEY).get() + " with a payload of: " + packet.payloadToString(), null);
                }
            }
        }
    }
}
