package io.netty.handler.codec.string;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class StringEncoder extends MessageToMessageEncoder<CharSequence> {

    private final Charset charset;

    public StringEncoder() {
        this(Charset.defaultCharset());
    }

    public StringEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, CharSequence charsequence, List<Object> list) throws Exception {
        if (charsequence.length() != 0) {
            list.add(Unpooled.copiedBuffer(charsequence, this.charset));
        }
    }
}
