package jonathansmith.jdpad.common.network.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import jonathansmith.jdpad.common.network.packet.PacketBuffer;

/**
 * Created by Jon on 26/03/14.
 *
 * Prepends the provided bytebuf with the length of the message
 */
public class MessagePrepender extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        ByteBuf source = (ByteBuf) o;
        int length = source.readableBytes();
        int lengthSize = PacketBuffer.getVarIntSize(length);

        if (lengthSize > 3) {
            throw new IllegalArgumentException("Unable to fit the provided packet length into 3 bytes");
        }

        else {
            PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
            packetBuffer.ensureWriteable(lengthSize + length);
            packetBuffer.writeVarIntToBuffer(length);
            packetBuffer.writeBytes(source, source.readerIndex(), length);
        }
    }
}
