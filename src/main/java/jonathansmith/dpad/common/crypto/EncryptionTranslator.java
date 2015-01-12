package jonathansmith.dpad.common.crypto;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Translator responsible for encoding and decoding messages using the provided cipher.
 */
public class EncryptionTranslator {

    private final Cipher cipher;
    private byte[] sourceBuffer = new byte[0];
    private byte[] outputBuffer = new byte[0];

    public EncryptionTranslator(Cipher cipher) {
        this.cipher = cipher;
    }

    public ByteBuf translateToUnencoded(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException {
        int length = buffer.readableBytes();
        byte[] bytes = this.readBytes(buffer);
        ByteBuf buf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(length));
        buf.writerIndex(this.cipher.update(bytes, 0, length, buf.array(), buf.arrayOffset()));
        return buf;
    }

    private byte[] readBytes(ByteBuf buffer) {
        int length = buffer.readableBytes();

        if (this.sourceBuffer.length < 1) {
            this.sourceBuffer = new byte[length];
        }

        buffer.readBytes(this.sourceBuffer, 0, length);
        return this.sourceBuffer;
    }

    public void translateToEncoded(ByteBuf msg, ByteBuf out) throws ShortBufferException {
        int length = msg.readableBytes();
        byte[] bytes = this.readBytes(msg);
        int encodedLength = this.cipher.getOutputSize(length);

        if (this.outputBuffer.length < encodedLength) {
            this.outputBuffer = new byte[encodedLength];
        }

        out.writeBytes(this.outputBuffer, 0, this.cipher.update(bytes, 0, length, this.outputBuffer));
    }
}
