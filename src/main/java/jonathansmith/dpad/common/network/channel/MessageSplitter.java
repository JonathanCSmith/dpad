package jonathansmith.dpad.common.network.channel;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import jonathansmith.dpad.common.network.packet.PacketBuffer;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Decodes provided bytes into a packet buffer for meaningful packet construction
 */
public class MessageSplitter extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
        byteBuf.markReaderIndex();
        byte[] bytes = new byte[3];

        for (int i = 0; i < bytes.length; i++) {

            // Ensure we can grab all the information in this decode frame
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            bytes[i] = byteBuf.readByte();

            if (bytes[i] >= 0) {
                // Grab the proposed length of this message
                int j = (new PacketBuffer(Unpooled.wrappedBuffer(bytes))).readVarIntFromBuffer();

                // Ensure we can grab the entire message
                if (byteBuf.readableBytes() < j) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                // Forward the entire message to the decoder
                objects.add(byteBuf.readBytes(j));
                return;
            }
        }

        throw new CorruptedFrameException("Message length longer than 21 bit");
    }
}
