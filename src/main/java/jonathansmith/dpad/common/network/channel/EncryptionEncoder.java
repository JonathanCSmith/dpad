package jonathansmith.dpad.common.network.channel;

import javax.crypto.Cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import jonathansmith.dpad.common.crypto.EncryptionTranslator;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Encodes messages in the pipeline.
 */
public class EncryptionEncoder extends MessageToByteEncoder {

    private final EncryptionTranslator translator;

    public EncryptionEncoder(Cipher cipher) {
        this.translator = new EncryptionTranslator(cipher);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        this.translator.translateToEncoded((ByteBuf) msg, out);
    }
}
