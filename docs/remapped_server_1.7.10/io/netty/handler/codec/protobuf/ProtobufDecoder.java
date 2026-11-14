package io.netty.handler.codec.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final boolean HAS_PARSER;
    private final MessageLite prototype;
    private final ExtensionRegistry extensionRegistry;

    public ProtobufDecoder(MessageLite messagelite) {
        this(messagelite, (ExtensionRegistry) null);
    }

    public ProtobufDecoder(MessageLite messagelite, ExtensionRegistry extensionregistry) {
        if (messagelite == null) {
            throw new NullPointerException("prototype");
        } else {
            this.prototype = messagelite.getDefaultInstanceForType();
            this.extensionRegistry = extensionregistry;
        }
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        int i = bytebuf.readableBytes();
        byte[] abyte;
        int j;

        if (bytebuf.hasArray()) {
            abyte = bytebuf.array();
            j = bytebuf.arrayOffset() + bytebuf.readerIndex();
        } else {
            abyte = new byte[i];
            bytebuf.getBytes(bytebuf.readerIndex(), abyte, 0, i);
            j = 0;
        }

        if (this.extensionRegistry == null) {
            if (ProtobufDecoder.HAS_PARSER) {
                list.add(this.prototype.getParserForType().parseFrom(abyte, j, i));
            } else {
                list.add(this.prototype.newBuilderForType().mergeFrom(abyte, j, i).build());
            }
        } else if (ProtobufDecoder.HAS_PARSER) {
            list.add(this.prototype.getParserForType().parseFrom(abyte, j, i, this.extensionRegistry));
        } else {
            list.add(this.prototype.newBuilderForType().mergeFrom(abyte, j, i, this.extensionRegistry).build());
        }

    }

    static {
        boolean flag = false;

        try {
            MessageLite.class.getDeclaredMethod("getParserForType");
            flag = true;
        } catch (Throwable throwable) {
            ;
        }

        HAS_PARSER = flag;
    }
}
