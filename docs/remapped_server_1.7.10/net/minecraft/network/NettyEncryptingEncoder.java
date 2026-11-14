package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class NettyEncryptingEncoder extends MessageToByteEncoder {

    private final NettyEncryptionTranslator field_150750_a;

    public NettyEncryptingEncoder(Cipher cipher) {
        this.field_150750_a = new NettyEncryptionTranslator(cipher);
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) {
        this.field_150750_a.func_150504_a(bytebuf, bytebuf1);
    }
}
