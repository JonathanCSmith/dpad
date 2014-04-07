package jonathansmith.dpad.common.network.channel;

import java.util.List;

import javax.crypto.Cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import jonathansmith.dpad.common.crypto.EncryptionTranslator;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Channel handler responsible for decoding encryption.
 */
public class EncryptionDecoder extends MessageToMessageDecoder {

    private final EncryptionTranslator translator;

    public EncryptionDecoder(Cipher cipher) {
        this.translator = new EncryptionTranslator(cipher);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        out.add(this.translator.translateToUnencoded(ctx, (ByteBuf) msg));
    }
}
